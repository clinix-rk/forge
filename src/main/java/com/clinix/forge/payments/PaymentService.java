package com.clinix.forge.payments;

import com.clinix.forge.core.exception.DuplicateResourceException;
import com.clinix.forge.core.exception.ResourceNotFoundException;
import com.clinix.forge.core.pdf.PdfGenerationService;
import com.clinix.forge.core.pdf.dto.ReceiptPdfData;
import com.clinix.forge.core.utils.NumberToWords;
import com.clinix.forge.patient.entity.PatientEntity;
import com.clinix.forge.patient.repositories.PatientRepository;
import com.clinix.forge.payments.dto.CreatePaymentRequest;
import com.clinix.forge.payments.dto.PaymentResponse;
import com.clinix.forge.payments.dto.UpdatePaymentRequest;
import com.clinix.forge.payments.entity.PaymentEntity;
import com.clinix.forge.payments.entity.PaymentMethod;
import com.clinix.forge.treatment.TreatmentEntity;
import com.clinix.forge.treatment.TreatmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.thymeleaf.context.Context;

import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PatientRepository patientRepository;
    private final TreatmentRepository treatmentRepository;
    private final PaymentMapper paymentMapper;

    private final PdfGenerationService pdfGenerationService;

    @Transactional(rollbackFor = Exception.class)
    public PaymentResponse createPayment(Long patientId, CreatePaymentRequest request) {
        log.debug("Adding payment for patient : {}", patientId);

        TreatmentEntity treatment = treatmentRepository.findById(request.treatmentId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Treatment id %d not found", request.treatmentId())));

        PatientEntity patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Patient id %d not found", patientId)));

        if (paymentRepository.findByTreatmentId(request.treatmentId()).isPresent()) {
            throw new DuplicateResourceException(String.format("Payment for treatment id %d already exists", request.treatmentId()));
        }

        PaymentEntity entity = paymentMapper.toEntity(request);
        entity.setPatient(patient);
        entity.setTreatment(treatment);

        PaymentEntity saved = paymentRepository.save(entity);
        log.debug("Payment id {} created", saved.getId());
        return paymentMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponse> getAllPayments(Long patientId, int pageNo, int pageSize) {
        log.debug("Reading payments patient id {}", patientId);
        Sort sort = Sort.by(
                Sort.Order.desc("treatment.date"),
                Sort.Order.desc("createdAt")
        );
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, sort);
        Page<PaymentEntity> page = (patientId != null)
                ? paymentRepository.findAllByPatientId(patientId, pageRequest)
                : paymentRepository.findAll(pageRequest);

        return page.map(paymentMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long patientId, Long id) {
        log.debug("Reading payment id {} for patient id {}", id, patientId);
        PaymentEntity entity = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Payment id %d not found", id)));
        return paymentMapper.toResponse(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public PaymentResponse updatePaymentById(Long patientId, Long id, UpdatePaymentRequest request) {
        log.debug("Updating payment id {} for patient id {}", id, patientId);
        PaymentEntity entity = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Payment id %d not found", id)));

        paymentMapper.updateEntityFromRequest(request, entity);
        PaymentEntity updated = paymentRepository.save(entity);
        log.debug("Updated payment id : {}", updated.getId());
        return paymentMapper.toResponse(updated);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deletePaymentById(Long patientId, Long id) {
        log.debug("Deleting payment id : {}", id);
        if (!paymentRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format("Payment id %d not found", id));
        }
        paymentRepository.deleteById(id);
        log.debug("Deleted payment id : {}", id);
    }

    @Transactional(rollbackFor = Exception.class)
    public byte[] generateSinglePaymentReceipt(Long patientId, Long id) {
        log.debug("Generating single payment receipt for payment id : {}", id);
        PaymentEntity entity = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Payment id %d not found", id)));

        long amount = entity.getAmount().longValue();
        String amountString = NumberToWords.formatIndian(amount);
        String amountInWords = NumberToWords.convert(amount);
        String receiptNo = paymentMapper.generateReceiptNo(entity);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateStr = entity.getTreatment().getDate().format(formatter);

        boolean printStamp = entity.getMethod() == PaymentMethod.CASH && entity.getAmount().longValue() >= 5000;

        ReceiptPdfData data = new ReceiptPdfData(
                receiptNo,
                dateStr,
                entity.getPatient().getName(),
                entity.getTreatmentDetails(),
                amountString,
                amountInWords,
                entity.getMethod().name(),
                printStamp
        );

        Context context = new Context();
        context.setVariable("receipt", data);

        try {
            log.debug("Generated single payment receipt for payment id : {}", id);
            return pdfGenerationService.generatePdf("pdf/receipt", context);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
