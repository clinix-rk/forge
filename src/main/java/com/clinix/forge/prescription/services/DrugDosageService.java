package com.clinix.forge.prescription;

import com.clinix.forge.core.exception.DuplicateResourceException;
import com.clinix.forge.core.exception.ResourceNotFoundException;
import com.clinix.forge.core.payload.PaginatedPayload;
import com.clinix.forge.prescription.dto.CreateDrugDosageRequest;
import com.clinix.forge.prescription.dto.DrugDosageResponse;
import com.clinix.forge.prescription.dto.UpdateDrugDosageRequest;
import com.clinix.forge.prescription.entity.DrugDosageEntity;
import com.clinix.forge.prescription.repositories.DrugDosageRepository;
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
public class DrugDosageService {

    private final DrugDosageRepository drugDosageRepository;
    private final PrescriptionMapper prescriptionMapper;

    @Transactional(rollbackFor = Exception.class)
    public DrugDosageResponse createDrugDosage(CreateDrugDosageRequest request) {
        log.info("Creating drug dosage pattern: {}", request.dosage());

        if (drugDosageRepository.findByDosage(request.dosage()).isPresent()) {
            throw new DuplicateResourceException("Drug dosage pattern '" + request.dosage() + "' already exists");
        }

        DrugDosageEntity entity = prescriptionMapper.toDosageEntity(request);
        DrugDosageEntity saved = drugDosageRepository.save(entity);
        log.info("Drug dosage created with ID: {}", saved.getId());
        return prescriptionMapper.toDosageResponse(saved);
    }

    @Transactional(readOnly = true)
    public PaginatedPayload<DrugDosageResponse> getAllDrugDosages(int pageNo, int pageSize) {
        log.debug("Fetching drug dosages - PageNo: {}, PageSize: {}", pageNo, pageSize);
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<DrugDosageEntity> page = drugDosageRepository.findAll(pageRequest);

        List<DrugDosageResponse> responses = page.getContent().stream()
                .map(prescriptionMapper::toDosageResponse)
                .toList();

        return PaginatedPayload.of(responses, page);
    }

    @Transactional(readOnly = true)
    public DrugDosageResponse getDrugDosageById(Long id) {
        log.debug("Fetching drug dosage with ID: {}", id);
        DrugDosageEntity entity = drugDosageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Drug dosage not found with ID: " + id));
        return prescriptionMapper.toDosageResponse(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public DrugDosageResponse updateDrugDosageById(Long id, UpdateDrugDosageRequest request) {
        log.info("Updating drug dosage with ID: {}", id);
        DrugDosageEntity entity = drugDosageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Drug dosage not found with ID: " + id));

        if (!entity.getDosage().equals(request.dosage()) && drugDosageRepository.findByDosage(request.dosage()).isPresent()) {
            throw new DuplicateResourceException("Drug dosage pattern '" + request.dosage() + "' already exists");
        }

        prescriptionMapper.updateDosageFromRequest(request, entity);
        DrugDosageEntity updated = drugDosageRepository.save(entity);
        log.info("Drug dosage updated with ID: {}", updated.getId());
        return prescriptionMapper.toDosageResponse(updated);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteDrugDosageById(Long id) {
        log.info("Deleting drug dosage with ID: {}", id);
        if (!drugDosageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Drug dosage not found with ID: " + id);
        }
        drugDosageRepository.deleteById(id);
        log.info("Drug dosage deleted: {}", id);
    }
}
