package com.clinix.forge.complain.service;

import com.clinix.forge.complain.mapper.ComplainMapper;
import com.clinix.forge.complain.dto.ComplainResponse;
import com.clinix.forge.complain.dto.CreateComplainRequest;
import com.clinix.forge.complain.dto.UpdateComplainRequest;
import com.clinix.forge.complain.entity.ComplainCategoryEntity;
import com.clinix.forge.complain.entity.ComplainEntity;
import com.clinix.forge.complain.repository.ComplainCategoryRepository;
import com.clinix.forge.complain.repository.ComplainRepository;
import com.clinix.forge.core.exception.ResourceNotFoundException;
import com.clinix.forge.core.payload.PaginatedPayload;
import com.clinix.forge.patient.entity.PatientEntity;
import com.clinix.forge.patient.repositories.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class ComplainService {

    private final ComplainRepository complainRepository;
    private final ComplainCategoryRepository complainCategoryRepository;
    private final PatientRepository patientRepository;
    private final ComplainMapper complainMapper;

    @Transactional(rollbackFor = Exception.class)
    public ComplainResponse createComplain(CreateComplainRequest request) {
        log.info("Creating complain for patient ID: {}", request.patientId());

        PatientEntity patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + request.patientId()));

        ComplainCategoryEntity category = complainCategoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Complain category not found with ID: " + request.categoryId()));

        ComplainEntity entity = complainMapper.toEntity(request);
        entity.setPatient(patient);
        entity.setCategory(category);

        ComplainEntity saved = complainRepository.save(entity);
        log.info("Complain created with ID: {}", saved.getId());
        return complainMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PaginatedPayload<ComplainResponse> getAllComplains(int pageNo, int pageSize) {
        return getAllComplains(null, pageNo, pageSize);
    }

    @Transactional(readOnly = true)
    public PaginatedPayload<ComplainResponse> getAllComplains(Long patientId, int pageNo, int pageSize) {
        log.debug("Fetching complains - PatientId: {}, PageNo: {}, PageSize: {}", patientId, pageNo, pageSize);
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<ComplainEntity> complainPage = (patientId != null)
                ? complainRepository.findByPatientId(patientId, pageRequest)
                : complainRepository.findAll(pageRequest);

        List<ComplainResponse> responses = complainPage.getContent().stream()
                .map(complainMapper::toResponse)
                .toList();

        return PaginatedPayload.of(responses, complainPage);
    }

    @Transactional(readOnly = true)
    public ComplainResponse getComplainById(Long id) {
        log.debug("Fetching complain with ID: {}", id);
        ComplainEntity complain = complainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complain not found with ID: " + id));
        return complainMapper.toResponse(complain);
    }

    @Transactional(rollbackFor = Exception.class)
    public ComplainResponse updateComplainById(Long id, UpdateComplainRequest request) {
        log.info("Updating complain with ID: {}", id);
        ComplainEntity complain = complainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complain not found with ID: " + id));

        ComplainCategoryEntity category = complainCategoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Complain category not found with ID: " + request.categoryId()));

        complainMapper.updateEntityFromRequest(request, complain);
        complain.setCategory(category);

        ComplainEntity updated = complainRepository.save(complain);
        log.info("Complain updated with ID: {}", updated.getId());
        return complainMapper.toResponse(updated);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteComplainById(Long id) {
        log.info("Deleting complain with ID: {}", id);
        if (!complainRepository.existsById(id)) {
            throw new ResourceNotFoundException("Complain not found with ID: " + id);
        }
        complainRepository.deleteById(id);
        log.info("Complain deleted successfully: {}", id);
    }
}
