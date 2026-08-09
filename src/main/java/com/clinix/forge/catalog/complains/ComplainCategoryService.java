package com.clinix.forge.catalog.complains;

import com.clinix.forge.catalog.complains.dto.ComplainCategoryResponse;
import com.clinix.forge.catalog.complains.dto.CreateComplainCategoryRequest;
import com.clinix.forge.catalog.complains.dto.UpdateComplainCategoryRequest;
import com.clinix.forge.core.exception.DuplicateResourceException;
import com.clinix.forge.core.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class ComplainCategoryService {

    private final ComplainCategoryRepository complainCategoryRepository;
    private final ComplainCategoryMapper complainCategoryMapper;

    @Transactional(rollbackFor = Exception.class)
    public ComplainCategoryResponse createComplainCategory(CreateComplainCategoryRequest request) {
        log.debug("Creating complain category: { name: {} }", request.name());

        ComplainCategoryEntity parent = null;
        if (request.parentId() != null) {
            parent = complainCategoryRepository.findById(request.parentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found with Id : {}" + request.parentId()));
        }

        if (isDuplicateWithSameParent(request.name(), request.parentId(), null)) {
            throw new DuplicateResourceException("Complain category with name '" + request.name() + "' already exists under this parent");
        }

        ComplainCategoryEntity entity = complainCategoryMapper.toComplainCategoryEntity(request);
        entity.setParent(parent);

        ComplainCategoryEntity saved = complainCategoryRepository.save(entity);

        log.debug("Created complain category : { id: {} }", saved.getId());

        return complainCategoryMapper.toComplainCategoryResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<ComplainCategoryResponse> getPaginatedComplainCategories(int pageNo, int pageSize) {
        log.debug("Fetching complain categories : { pageNo: {}, pageSize: {} }", pageNo, pageSize);

        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<ComplainCategoryEntity> categoryPage = complainCategoryRepository.findAll(pageRequest);

        return categoryPage.map(complainCategoryMapper::toComplainCategoryResponse);
    }

    @Transactional(readOnly = true)
    public ComplainCategoryResponse getComplainCategoryById(Long id) {
        log.debug("Fetching complain category : { id: {} }", id);

        ComplainCategoryEntity category = complainCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No complain category found with id: " + id));

        return complainCategoryMapper.toComplainCategoryResponse(category);
    }

    @Transactional(rollbackFor = Exception.class)
    public ComplainCategoryResponse updateComplainCategoryById(Long id, UpdateComplainCategoryRequest request) {
        log.info("Updating complain category : { id: {} }", id);
        ComplainCategoryEntity category = complainCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No complain category found with id : " + id));

        ComplainCategoryEntity parent = null;
        if (request.parentId() != null) {
            if (request.parentId().equals(id)) {
                throw new IllegalArgumentException("A category cannot be its own parent");
            }
            parent = complainCategoryRepository.findById(request.parentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found with ID: " + request.parentId()));
        }

        if (isDuplicateWithSameParent(request.name(), request.parentId(), id)) {
            throw new DuplicateResourceException("Complain category with name '" + request.name() + "' already exists under this parent");
        }

        complainCategoryMapper.updateComplainCategoryFromRequest(request, category);
        category.setParent(parent);

        ComplainCategoryEntity updated = complainCategoryRepository.save(category);
        log.info("Complain category updated with ID: {}", updated.getId());
        return complainCategoryMapper.toComplainCategoryResponse(updated);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteComplainCategoryById(Long id) {
        log.debug("Deleting complain category : { id: {} }", id);

        if (!complainCategoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Complain category not found with ID: " + id);
        }

        log.info("Complain category deleted: { id : {} }", id);
        complainCategoryRepository.deleteById(id);

        return true;
    }

    private boolean isDuplicateWithSameParent(String name, Long parentId, Long excludeId) {
        Optional<ComplainCategoryEntity> existing = parentId == null
                ? complainCategoryRepository.findByNameAndParentIsNull(name)
                : complainCategoryRepository.findByNameAndParentId(name, parentId);

        return existing.isPresent() && (excludeId == null || !existing.get().getId().equals(excludeId));
    }
}
