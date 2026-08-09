package com.clinix.forge.patient.repositories;

import com.clinix.forge.patient.entity.PatientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<PatientEntity, Long> {
    boolean existsByCaseNo(String caseNo);

    Optional<PatientEntity> findByCaseNo(String caseNo);

    @Query("SELECT DISTINCT p FROM PatientEntity p LEFT JOIN p.phoneNumbers ph WHERE " +
            "(CAST(:name AS string) IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:name AS string), '%'))) AND " +
            "(CAST(:caseNo AS string) IS NULL OR LOWER(p.caseNo) LIKE LOWER(CONCAT('%', CAST(:caseNo AS string), '%'))) AND " +
            "(CAST(:phoneNo AS string) IS NULL OR ph.phoneNumber LIKE CONCAT('%', CAST(:phoneNo AS string), '%'))")
    Page<PatientEntity> searchPatients(
            @Param("name") String name,
            @Param("caseNo") String caseNo,
            @Param("phoneNo") String phoneNo,
            Pageable pageable);
}
