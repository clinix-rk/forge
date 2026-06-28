package com.clinix.forge.prescription;

import com.clinix.forge.prescription.entity.PrescriptionMedicineEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing PrescriptionMedicineEntity objects.
 */
public interface PrescriptionMedicineRepository extends JpaRepository<PrescriptionMedicineEntity, Long> {
}
