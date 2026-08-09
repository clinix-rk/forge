package com.clinix.forge.catalog.complains;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing ComplainCategoryEntity objects.
 */
public interface ComplainCategoryRepository extends JpaRepository<ComplainCategoryEntity, Long> {
    Optional<ComplainCategoryEntity> findByNameAndParentId(String name, Long parentId);

    Optional<ComplainCategoryEntity> findByNameAndParentIsNull(String name);
}
