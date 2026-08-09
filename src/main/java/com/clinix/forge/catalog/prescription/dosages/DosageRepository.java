package com.clinix.forge.catalog.prescription.dosages;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing DosageEntity objects.
 */
public interface DosageRepository extends JpaRepository<DosageEntity, Long> {
    Optional<DosageEntity> findByDosage(String dosage);
}
