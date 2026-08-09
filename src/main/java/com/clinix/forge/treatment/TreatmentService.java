package com.clinix.forge.treatment;

import com.clinix.forge.catalog.treatments.TreatmentCategoryEntity;
import com.clinix.forge.catalog.treatments.TreatmentCategoryRepository;
import com.clinix.forge.core.exception.ResourceNotFoundException;
import com.clinix.forge.finance.PaymentRepository;
import com.clinix.forge.finance.entity.PaymentEntity;
import com.clinix.forge.finance.entity.PaymentMethod;
import com.clinix.forge.patient.entity.PatientEntity;
import com.clinix.forge.patient.repositories.PatientRepository;
import com.clinix.forge.treatment.dto.CreateTreatmentRequest;
import com.clinix.forge.treatment.dto.TreatmentResponse;
import com.clinix.forge.treatment.dto.UpdateTreatmentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class TreatmentService {

    private final TreatmentRepository treatmentRepository;
    private final TreatmentCategoryRepository treatmentCategoryRepository;
    private final PatientRepository patientRepository;
    private final TreatmentMapper treatmentMapper;
    private final PaymentRepository paymentRepository;

    private final String getDisplayCategory(TreatmentCategoryEntity category) {
        StringBuilder displayCategory = new StringBuilder(category.getName());

        while (category.getParent() != null) {
            displayCategory.insert(0, category.getName() + " / ");
            category = category.getParent();
        }

        return displayCategory.toString();
    }

    @Transactional(rollbackFor = Exception.class)
    public TreatmentResponse createTreatment(CreateTreatmentRequest request) {
        log.info("Creating treatment for patient ID: {}", request.patientId());

        PatientEntity patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + request.patientId()));

        TreatmentCategoryEntity category = treatmentCategoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Treatment category not found with ID: " + request.categoryId()));

        TreatmentEntity entity = treatmentMapper.toEntity(request);
        entity.setPatient(patient);
        entity.setCategory(category);

        TreatmentEntity saved = treatmentRepository.save(entity);
        log.info("Treatment created with ID: {}", saved.getId());

        String doctorPrefix = patient.getDoctor() != null ? patient.getDoctor().getCaseNoPrefix() : "D";
        LocalDate date = request.date() != null ? request.date() : LocalDate.now();
        int year = date.getYear();
        int month = date.getMonthValue();
        String financialYear = (month >= 4) ? (year + "-" + (year + 1)) : ((year - 1) + "-" + year);
        int serial = paymentRepository.findMaxSerialByFinancialYearAndDoctorIdentityCharacter(financialYear, doctorPrefix).orElse(0) + 1;

        PaymentEntity dummyPayment = new PaymentEntity(
                patient,
                doctorPrefix,
                financialYear,
                serial,
                saved,
                0.0,
                PaymentMethod.CASH,
                ""
        );

        paymentRepository.save(dummyPayment);

        saved.setPayment(dummyPayment);

        return treatmentMapper.toResponse(saved, getDisplayCategory(saved.getCategory()));
    }

    @Transactional(readOnly = true)
    public Page<TreatmentResponse> getAllTreatments(int pageNo, int pageSize) {
        return getAllTreatments(null, pageNo, pageSize);
    }

    @Transactional(readOnly = true)
    public Page<TreatmentResponse> getAllTreatments(Long patientId, int pageNo, int pageSize) {
        log.debug("Fetching treatments - PatientId: {}, PageNo: {}, PageSize: {}", patientId, pageNo, pageSize);
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<TreatmentEntity> page = (patientId != null)
                ? treatmentRepository.findByPatientId(patientId, pageRequest)
                : treatmentRepository.findAll(pageRequest);

        return page.map(treatment -> treatmentMapper.toResponse(treatment, getDisplayCategory(treatment.getCategory())));
    }

    @Transactional(readOnly = true)
    public TreatmentResponse getTreatmentById(Long id) {
        log.debug("Fetching treatment with ID: {}", id);
        TreatmentEntity treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment not found with ID: " + id));
        return treatmentMapper.toResponse(treatment, getDisplayCategory(treatment.getCategory()));
    }

    @Transactional(rollbackFor = Exception.class)
    public TreatmentResponse updateTreatmentById(Long id, UpdateTreatmentRequest request) {
        log.info("Updating treatment with ID: {}", id);
        TreatmentEntity treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment not found with ID: " + id));

        TreatmentCategoryEntity category = treatmentCategoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Treatment category not found with ID: " + request.categoryId()));

        treatmentMapper.updateEntityFromRequest(request, treatment);
        treatment.setCategory(category);

        TreatmentEntity updated = treatmentRepository.save(treatment);
        log.info("Treatment updated with ID: {}", updated.getId());
        return treatmentMapper.toResponse(updated, getDisplayCategory(updated.getCategory()));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteTreatmentById(Long id) {
        log.info("Deleting treatment with ID: {}", id);
        if (!treatmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Treatment not found with ID: " + id);
        }
        treatmentRepository.deleteById(id);
        log.info("Treatment deleted successfully: {}", id);
    }
}
