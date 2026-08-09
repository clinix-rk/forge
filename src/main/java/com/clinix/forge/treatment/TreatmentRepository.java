package com.clinix.forge.treatment;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for managing TreatmentEntity objects.
 */
public interface TreatmentRepository extends JpaRepository<TreatmentEntity, Long> {

    @Query("""
            SELECT t FROM TreatmentEntity t
            JOIN FETCH t.patient p
            JOIN FETCH p.doctor d
            JOIN FETCH t.category
            WHERE t.date BETWEEN :from AND :to
            AND (:doctorId IS NULL OR d.id = :doctorId)
            ORDER BY t.date ASC, t.id ASC
            """)
    List<TreatmentEntity> findAllForForm3C(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("doctorId") Long doctorId
    );

    org.springframework.data.domain.Page<TreatmentEntity> findByPatientId(Long patientId, org.springframework.data.domain.Pageable pageable);
}

