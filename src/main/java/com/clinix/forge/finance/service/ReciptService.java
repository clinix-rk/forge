package com.clinix.forge.finance;

import com.clinix.forge.core.exception.DuplicateResourceException;
import com.clinix.forge.core.exception.ResourceNotFoundException;
import com.clinix.forge.core.payload.PaginatedPayload;
import com.clinix.forge.finance.dto.CreateReciptRequest;
import com.clinix.forge.finance.dto.ReciptResponse;
import com.clinix.forge.finance.dto.UpdateReciptRequest;
import com.clinix.forge.finance.entity.ReciptEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import com.clinix.forge.core.pdf.PdfGenerationService;
import com.clinix.forge.core.pdf.dto.ReceiptLineItem;
import com.clinix.forge.core.pdf.dto.ReceiptPdfData;
import com.clinix.forge.finance.entity.PaymentEntity;
import org.thymeleaf.context.Context;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class ReciptService {

    private final ReciptRepository reciptRepository;
    private final PaymentRepository paymentRepository;
    private final PdfGenerationService pdfGenerationService;
    private final ReciptMapper reciptMapper;

    @Transactional(rollbackFor = Exception.class)
    public ReciptResponse createRecipt(CreateReciptRequest request) {
        log.info("Creating recipt - Doctor identity: {}, FY: {}, Serial: {}",
                request.doctorIdentityCharacter(), request.financialYear(), request.serial());

        if (reciptRepository.findByFinancialYearAndDoctorIdentityCharacterAndSerial(
                request.financialYear(), request.doctorIdentityCharacter(), request.serial()).isPresent()) {
            throw new DuplicateResourceException("Recipt already exists for the given doctor prefix, financial year and serial number.");
        }

        ReciptEntity entity = reciptMapper.toEntity(request);
        ReciptEntity saved = reciptRepository.save(entity);
        log.info("Recipt created with ID: {}", saved.getId());
        return reciptMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PaginatedPayload<ReciptResponse> getAllRecipts(int pageNo, int pageSize) {
        return getAllRecipts(null, pageNo, pageSize);
    }

    @Transactional(readOnly = true)
    public PaginatedPayload<ReciptResponse> getAllRecipts(Long patientId, int pageNo, int pageSize) {
        log.debug("Fetching recipts - PatientId: {}, PageNo: {}, PageSize: {}", patientId, pageNo, pageSize);
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<ReciptEntity> page = (patientId != null)
                ? reciptRepository.findByPatientId(patientId, pageRequest)
                : reciptRepository.findAll(pageRequest);

        List<ReciptResponse> responses = page.getContent().stream()
                .map(reciptMapper::toResponse)
                .toList();

        return PaginatedPayload.of(responses, page);
    }

    @Transactional(readOnly = true)
    public ReciptResponse getReciptById(Long id) {
        log.debug("Fetching recipt with ID: {}", id);
        ReciptEntity entity = reciptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipt not found with ID: " + id));
        return reciptMapper.toResponse(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public ReciptResponse updateReciptById(Long id, UpdateReciptRequest request) {
        log.info("Updating recipt with ID: {}", id);
        ReciptEntity entity = reciptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipt not found with ID: " + id));

        if (!(entity.getFinancialYear().equals(request.financialYear()) &&
                entity.getDoctorIdentityCharacter().equals(request.doctorIdentityCharacter()) &&
                entity.getSerial().equals(request.serial())) &&
                reciptRepository.findByFinancialYearAndDoctorIdentityCharacterAndSerial(
                        request.financialYear(), request.doctorIdentityCharacter(), request.serial()).isPresent()) {
            throw new DuplicateResourceException("Recipt already exists with the updated doctor prefix, financial year, and serial number.");
        }

        reciptMapper.updateEntityFromRequest(request, entity);
        ReciptEntity updated = reciptRepository.save(entity);
        log.info("Recipt updated with ID: {}", updated.getId());
        return reciptMapper.toResponse(updated);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteReciptById(Long id) {
        log.info("Deleting recipt with ID: {}", id);
        if (!reciptRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recipt not found with ID: " + id);
        }
        reciptRepository.deleteById(id);
        log.info("Recipt deleted: {}", id);
    }

    @Transactional(readOnly = true)
    public byte[] generateReceiptPdf(Long id) {
        log.info("Generating PDF for Receipt ID: {}", id);
        ReciptEntity recipt = reciptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipt not found with ID: " + id));

        List<PaymentEntity> payments = paymentRepository.findAllByReciptId(id);

        String doctorName = "N/A";
        if (!payments.isEmpty() && payments.get(0).getTreatment() != null
                && payments.get(0).getTreatment().getPatient() != null
                && payments.get(0).getTreatment().getPatient().getDoctor() != null) {
            doctorName = payments.get(0).getTreatment().getPatient().getDoctor().getName();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        List<ReceiptLineItem> lines = payments.stream()
                .map(payment -> {
                    String dateStr = payment.getTreatment() != null && payment.getTreatment().getDate() != null
                            ? payment.getTreatment().getDate().format(formatter)
                            : "—";
                    String workDone = payment.getTreatment() != null
                            ? payment.getTreatment().getCategory().getName() + 
                              (payment.getTreatment().getDetails() != null && !payment.getTreatment().getDetails().trim().isEmpty()
                               ? " " + payment.getTreatment().getDetails() : "")
                            : "Unknown";
                    return new ReceiptLineItem(dateStr, workDone, payment.getAmount());
                })
                .toList();

        double total = payments.stream().mapToDouble(PaymentEntity::getAmount).sum();

        ReceiptPdfData data = new ReceiptPdfData(doctorName, lines, total);
        Context context = new Context();
        context.setVariable("receipt", data);

        return pdfGenerationService.generatePdf("pdf/receipt", context);
    }
}
