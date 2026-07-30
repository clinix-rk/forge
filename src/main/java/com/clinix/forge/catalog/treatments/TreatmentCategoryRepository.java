package com.clinix.forge.catalog.treatments;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing TreatmentCategoryEntity objects.
 */
public interface TreatmentCategoryRepository extends JpaRepository<TreatmentCategoryEntity, Long> {
    Optional<TreatmentCategoryEntity> findByNameAndParentId(String name, Long parentId);

    Optional<TreatmentCategoryEntity> findByNameAndParentIsNull(String name);
}
