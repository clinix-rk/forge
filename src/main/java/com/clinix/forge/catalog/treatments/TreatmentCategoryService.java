package com.clinix.forge.catalog.treatments;

import com.clinix.forge.catalog.treatments.dto.CreateTreatmentCategoryRequest;
import com.clinix.forge.catalog.treatments.dto.TreatmentCategoryResponse;
import com.clinix.forge.catalog.treatments.dto.UpdateTreatmentCategoryRequest;
import com.clinix.forge.core.exception.DuplicateResourceException;
import com.clinix.forge.core.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class TreatmentCategoryService {

    private final TreatmentCategoryRepository treatmentCategoryRepository;
    private final TreatmentCategoryMapper treatmentCategoryMapper;

    @Transactional(rollbackFor = Exception.class)
    public TreatmentCategoryResponse createTreatmentCategory(CreateTreatmentCategoryRequest request) {
        log.debug("Creating treatment category: {}", request);

        TreatmentCategoryEntity parent = null;
        if (request.parentId() != null) {
            parent = treatmentCategoryRepository.findById(request.parentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found with ID: " + request.parentId()));
        }

        if (isDuplicateWithSameParent(request.name(), request.parentId(), null)) {
            throw new DuplicateResourceException("Treatment category with name '" + request.name() + "' already exists under the same parent");
        }

        TreatmentCategoryEntity entity = treatmentCategoryMapper.toTreatmentCategoryEntity(request);
        entity.setParent(parent);

        TreatmentCategoryEntity saved = treatmentCategoryRepository.save(entity);

        return treatmentCategoryMapper.toTreatmentCategoryResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<TreatmentCategoryResponse> getAllTreatmentCategories(Long parentId) {
        log.debug("Fetching treatment categories - Parent ID: {}", parentId);

        List<TreatmentCategoryEntity> categories = treatmentCategoryRepository.findAll();

        return categories
                .stream()
                .map(treatmentCategoryMapper::toTreatmentCategoryResponse)
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public TreatmentCategoryResponse updateTreatmentCategoryById(Long id, UpdateTreatmentCategoryRequest request) {
        log.info("Updating treatment category with ID: {}", id);
        TreatmentCategoryEntity category = treatmentCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment category not found with ID: " + id));

        TreatmentCategoryEntity parent = null;
        if (request.parentId() != null) {
            if (request.parentId().equals(id)) {
                throw new IllegalArgumentException("A category cannot be its own parent");
            }
            parent = treatmentCategoryRepository.findById(request.parentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found with ID: " + request.parentId()));
        }

        if (isDuplicateWithSameParent(request.name(), request.parentId(), id)) {
            throw new DuplicateResourceException("Treatment category with name '" + request.name() + "' already exists under the same parent");
        }

        treatmentCategoryMapper.updateTreatmentCategory(request, category);
        category.setParent(parent);

        TreatmentCategoryEntity updated = treatmentCategoryRepository.save(category);

        return treatmentCategoryMapper.toTreatmentCategoryResponse(updated);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTreatmentCategoryById(Long id) {
        log.info("Deleting treatment category with ID: {}", id);
        if (!treatmentCategoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Treatment category not found with ID: " + id);
        }

        treatmentCategoryRepository.deleteById(id);

        return true;
    }

    private boolean isDuplicateWithSameParent(String name, Long parentId, Long excludeId) {
        Optional<TreatmentCategoryEntity> existing = parentId == null
                ? treatmentCategoryRepository.findByNameAndParentIsNull(name)
                : treatmentCategoryRepository.findByNameAndParentId(name, parentId);

        return existing.isPresent() && (excludeId == null || !existing.get().getId().equals(excludeId));
    }
}
