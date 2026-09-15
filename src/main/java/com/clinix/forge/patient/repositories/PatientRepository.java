package com.clinix.forge.patient.repositories;

import com.clinix.forge.patient.entity.PatientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<PatientEntity, Long> {
    Optional<PatientEntity> findByCaseNo(String caseNo);

    @Query("SELECT p.serial FROM PatientEntity p WHERE p.doctor.id = :doctorId ORDER BY p.serial DESC LIMIT 1")
    Optional<Integer> findMaxSerialByDoctorId(@Param("doctorId") Long doctorId);

    @Query("SELECT DISTINCT p FROM PatientEntity p LEFT JOIN p.phoneNumbers ph WHERE " +
            "CAST(:term AS string) IS NULL OR " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:term AS string), '%')) OR " +
            "LOWER(p.caseNo) LIKE LOWER(CONCAT('%', CAST(:term AS string), '%')) OR " +
//            "LOWER(p.serial) LIKE LOWER(CONCAT('%', CAST(:term AS string), '%')) OR " +
            "ph.phoneNumber LIKE CONCAT('%', CAST(:term AS string), '%')")
    Page<PatientEntity> searchPatients(@Param("term") String term, Pageable pageable);
}
