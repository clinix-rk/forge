package com.clinix.forge.finance.service;

import com.clinix.forge.core.exception.DuplicateResourceException;
import com.clinix.forge.core.exception.ResourceNotFoundException;
import com.clinix.forge.finance.PaymentMapper;
import com.clinix.forge.finance.PaymentRepository;
import com.clinix.forge.finance.dto.CreatePaymentRequest;
import com.clinix.forge.finance.dto.PaymentResponse;
import com.clinix.forge.finance.dto.UpdatePaymentRequest;
import com.clinix.forge.finance.entity.PaymentEntity;
import com.clinix.forge.patient.entity.PatientEntity;
import com.clinix.forge.patient.repositories.PatientRepository;
import com.clinix.forge.treatment.TreatmentEntity;
import com.clinix.forge.treatment.TreatmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PatientRepository patientRepository;
    private final TreatmentRepository treatmentRepository;
    private final PaymentMapper paymentMapper;

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
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
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
}
