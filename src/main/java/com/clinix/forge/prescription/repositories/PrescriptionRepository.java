package com.clinix.forge.prescription.repositories;

import com.clinix.forge.prescription.entity.PrescriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repository interface for managing PrescriptionEntity objects.
 */
public interface PrescriptionRepository extends JpaRepository<PrescriptionEntity, Long> {

    @Query("""
            SELECT p FROM PrescriptionEntity p
            LEFT JOIN FETCH p.prescriptionMedicines pm
            LEFT JOIN FETCH pm.medicine
            LEFT JOIN FETCH pm.dosage
            WHERE p.id = :id
            """)
    Optional<PrescriptionEntity> findByIdWithMedicines(@Param("id") Long id);

    org.springframework.data.domain.Page<PrescriptionEntity> findByPatientId(Long patientId, org.springframework.data.domain.Pageable pageable);
}

