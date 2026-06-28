package com.clinix.forge.finance;

import com.clinix.forge.core.exception.DuplicateResourceException;
import com.clinix.forge.core.exception.ResourceNotFoundException;
import com.clinix.forge.core.payload.PaginatedPayload;
import com.clinix.forge.finance.dto.CreatePaymentRequest;
import com.clinix.forge.finance.dto.EnrichedPaymentResponse;
import com.clinix.forge.finance.dto.PaymentResponse;
import com.clinix.forge.finance.dto.UpdatePaymentRequest;
import com.clinix.forge.finance.entity.PaymentEntity;
import com.clinix.forge.finance.entity.PaymentMethod;
import com.clinix.forge.finance.entity.ReciptEntity;
import com.clinix.forge.treatment.repository.TreatmentRepository;
import com.clinix.forge.treatment.entity.TreatmentEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;


@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReciptRepository reciptRepository;
    private final TreatmentRepository treatmentRepository;
    private final PaymentMapper paymentMapper;

    @Transactional(rollbackFor = Exception.class)
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        log.info("Creating payment for recipt ID: {} and treatment ID: {}", request.reciptId(), request.treatmentId());

        ReciptEntity recipt = reciptRepository.findById(request.reciptId())
                .orElseThrow(() -> new ResourceNotFoundException("Recipt not found with ID: " + request.reciptId()));

        TreatmentEntity treatment = treatmentRepository.findById(request.treatmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Treatment not found with ID: " + request.treatmentId()));

        if (paymentRepository.findByTreatmentId(request.treatmentId()).isPresent()) {
            throw new DuplicateResourceException("A payment has already been registered for treatment ID: " + request.treatmentId());
        }

        PaymentEntity entity = paymentMapper.toEntity(request);
        entity.setRecipt(recipt);
        entity.setTreatment(treatment);

        PaymentEntity saved = paymentRepository.save(entity);
        log.info("Payment created with ID: {}", saved.getId());
        return paymentMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PaginatedPayload<PaymentResponse> getAllPayments(int pageNo, int pageSize) {
        return getAllPayments(null, pageNo, pageSize);
    }

    @Transactional(readOnly = true)
    public PaginatedPayload<PaymentResponse> getAllPayments(Long patientId, int pageNo, int pageSize) {
        log.debug("Fetching payments - PatientId: {}, PageNo: {}, PageSize: {}", patientId, pageNo, pageSize);
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<PaymentEntity> page = (patientId != null)
                ? paymentRepository.findByPatientId(patientId, pageRequest)
                : paymentRepository.findAll(pageRequest);

        List<PaymentResponse> responses = page.getContent().stream()
                .map(paymentMapper::toResponse)
                .toList();

        return PaginatedPayload.of(responses, page);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {
        log.debug("Fetching payment with ID: {}", id);
        PaymentEntity entity = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + id));
        return paymentMapper.toResponse(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public PaymentResponse updatePaymentById(Long id, UpdatePaymentRequest request) {
        log.info("Updating payment with ID: {}", id);
        PaymentEntity entity = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + id));

        paymentMapper.updateEntityFromRequest(request, entity);
        PaymentEntity updated = paymentRepository.save(entity);
        log.info("Payment updated with ID: {}", updated.getId());
        return paymentMapper.toResponse(updated);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deletePaymentById(Long id) {
        log.info("Deleting payment with ID: {}", id);
        if (!paymentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Payment not found with ID: " + id);
        }
        paymentRepository.deleteById(id);
        log.info("Payment deleted: {}", id);
    }

    @Transactional(readOnly = true)
    public PaginatedPayload<EnrichedPaymentResponse> getEnrichedPayments(
            int pageNo, int pageSize, PaymentMethod method, LocalDate fromDate, LocalDate toDate, String search) {
        log.debug("Fetching enriched payments - PageNo: {}, PageSize: {}, Method: {}, FromDate: {}, ToDate: {}, Search: {}",
                pageNo, pageSize, method, fromDate, toDate, search);
        // Convert 1-based pageNo query param to 0-based JPA Pageable
        PageRequest pageRequest = PageRequest.of(pageNo - 1, pageSize);
        Page<PaymentEntity> page = paymentRepository.findEnrichedPayments(method, fromDate, toDate, search, pageRequest);

        List<EnrichedPaymentResponse> responses = page.getContent().stream()
                .map(paymentMapper::toEnrichedResponse)
                .toList();

        return PaginatedPayload.of(responses, page);
    }
}

