package com.clinix.forge.suggestion;

import com.clinix.forge.core.exception.ResourceNotFoundException;
import com.clinix.forge.core.payload.PaginatedPayload;
import com.clinix.forge.patient.entity.PatientEntity;
import com.clinix.forge.patient.repositories.PatientRepository;
import com.clinix.forge.suggestion.dto.CreateSuggestionRequest;
import com.clinix.forge.suggestion.dto.SuggestionResponse;
import com.clinix.forge.suggestion.dto.UpdateSuggestionRequest;
import com.clinix.forge.suggestion.entity.SuggestionEntity;
import com.clinix.forge.suggestion.entity.SuggestionStatus;
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
public class SuggestionService {

    private final SuggestionRepository suggestionRepository;
    private final PatientRepository patientRepository;
    private final SuggestionMapper suggestionMapper;

    @Transactional(rollbackFor = Exception.class)
    public SuggestionResponse createSuggestion(CreateSuggestionRequest request) {
        log.info("Creating suggestion for patient ID: {}", request.patientId());

        PatientEntity patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + request.patientId()));

        SuggestionEntity entity = suggestionMapper.toEntity(request);
        entity.setPatient(patient);
        if (entity.getStatus() == null) {
            entity.setStatus(SuggestionStatus.SUGGESTED);
        }

        SuggestionEntity savedSuggestion = suggestionRepository.save(entity);
        log.info("Suggestion created successfully with ID: {}", savedSuggestion.getId());
        return suggestionMapper.toResponse(savedSuggestion);
    }

    @Transactional(readOnly = true)
    public PaginatedPayload<SuggestionResponse> getAllSuggestions(int pageNo, int pageSize) {
        return getAllSuggestions(null, pageNo, pageSize);
    }

    @Transactional(readOnly = true)
    public PaginatedPayload<SuggestionResponse> getAllSuggestions(Long patientId, int pageNo, int pageSize) {
        log.debug("Fetching suggestions - PatientId: {}, PageNo: {}, PageSize: {}", patientId, pageNo, pageSize);
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<SuggestionEntity> suggestionPage = (patientId != null)
                ? suggestionRepository.findByPatientId(patientId, pageRequest)
                : suggestionRepository.findAll(pageRequest);

        List<SuggestionResponse> responses = suggestionPage.getContent().stream()
                .map(suggestionMapper::toResponse)
                .toList();

        return PaginatedPayload.of(responses, suggestionPage);
    }

    @Transactional(readOnly = true)
    public SuggestionResponse getSuggestionById(Long id) {
        log.debug("Fetching suggestion with ID: {}", id);
        SuggestionEntity suggestion = suggestionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Suggestion not found with ID: " + id));
        return suggestionMapper.toResponse(suggestion);
    }

    @Transactional(rollbackFor = Exception.class)
    public SuggestionResponse updateSuggestionById(Long id, UpdateSuggestionRequest request) {
        log.info("Updating suggestion with ID: {}", id);
        SuggestionEntity suggestion = suggestionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Suggestion not found with ID: " + id));

        suggestionMapper.updateEntityFromRequest(request, suggestion);

        SuggestionEntity updatedSuggestion = suggestionRepository.save(suggestion);
        log.info("Suggestion updated successfully with ID: {}", updatedSuggestion.getId());
        return suggestionMapper.toResponse(updatedSuggestion);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteSuggestionById(Long id) {
        log.info("Deleting suggestion with ID: {}", id);
        if (!suggestionRepository.existsById(id)) {
            log.warn("Suggestion not found for deletion with ID: {}", id);
            throw new ResourceNotFoundException("Suggestion not found with ID: " + id);
        }
        suggestionRepository.deleteById(id);
        log.info("Suggestion deleted successfully: {}", id);
    }
}
