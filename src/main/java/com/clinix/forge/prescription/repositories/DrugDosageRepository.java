package com.clinix.forge.prescription.repositories;

import com.clinix.forge.prescription.entity.DrugDosageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository interface for managing DrugDosageEntity objects.
 */
public interface DrugDosageRepository extends JpaRepository<DrugDosageEntity, Long> {
    Optional<DrugDosageEntity> findByDosage(String dosage);
}
