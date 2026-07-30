package com.clinix.forge.catalog.dosages;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing DrugDosageEntity objects.
 */
public interface DosageRepository extends JpaRepository<DrugDosageEntity, Long> {
    Optional<DrugDosageEntity> findByDosage(String dosage);
}
