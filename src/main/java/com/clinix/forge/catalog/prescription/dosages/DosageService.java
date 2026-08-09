package com.clinix.forge.catalog.prescription.dosages;

import com.clinix.forge.catalog.prescription.dosages.dto.CreateDrugDosageRequest;
import com.clinix.forge.catalog.prescription.dosages.dto.DrugDosageResponse;
import com.clinix.forge.catalog.prescription.dosages.dto.UpdateDrugDosageRequest;
import com.clinix.forge.core.exception.DuplicateResourceException;
import com.clinix.forge.core.exception.ResourceNotFoundException;
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
public class DosageService {

    private final DosageRepository dosageRepository;
    private final DosageMapper dosageMapper;

    @Transactional(rollbackFor = Exception.class)
    public DrugDosageResponse createDrugDosage(CreateDrugDosageRequest request) {
        log.info("Creating drug dosage pattern: {}", request.dosage());

        if (dosageRepository.findByDosage(request.dosage()).isPresent()) {
            throw new DuplicateResourceException("Drug dosage pattern '" + request.dosage() + "' already exists");
        }

        DosageEntity entity = dosageMapper.toDosageEntity(request);
        DosageEntity saved = dosageRepository.save(entity);
        log.info("Drug dosage created with ID: {}", saved.getId());
        return dosageMapper.toDosageResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<DrugDosageResponse> getAllDrugDosages(int pageNo, int pageSize) {
        log.debug("Fetching drug dosages - PageNo: {}, PageSize: {}", pageNo, pageSize);
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<DosageEntity> page = dosageRepository.findAll(pageRequest);
        return page.map(dosageMapper::toDosageResponse);
    }

    @Transactional(readOnly = true)
    public DrugDosageResponse getDrugDosageById(Long id) {
        log.debug("Fetching drug dosage with ID: {}", id);
        DosageEntity entity = dosageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Drug dosage not found with ID: " + id));
        return dosageMapper.toDosageResponse(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public DrugDosageResponse updateDrugDosageById(Long id, UpdateDrugDosageRequest request) {
        log.info("Updating drug dosage with ID: {}", id);
        DosageEntity entity = dosageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Drug dosage not found with ID: " + id));

        if (!entity.getDosage().equals(request.dosage()) && dosageRepository.findByDosage(request.dosage()).isPresent()) {
            throw new DuplicateResourceException("Drug dosage pattern '" + request.dosage() + "' already exists");
        }

        dosageMapper.updateDosageFromRequest(request, entity);
        DosageEntity updated = dosageRepository.save(entity);
        log.info("Drug dosage updated with ID: {}", updated.getId());
        return dosageMapper.toDosageResponse(updated);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteDrugDosageById(Long id) {
        log.info("Deleting drug dosage with ID: {}", id);
        if (!dosageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Drug dosage not found with ID: " + id);
        }
        dosageRepository.deleteById(id);
        log.info("Drug dosage deleted: {}", id);
    }
}
