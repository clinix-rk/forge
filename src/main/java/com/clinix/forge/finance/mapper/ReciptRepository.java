package com.clinix.forge.finance;

import com.clinix.forge.finance.entity.ReciptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

/**
 * Repository interface for managing ReciptEntity objects.
 */
public interface ReciptRepository extends JpaRepository<ReciptEntity, Long> {
    Optional<ReciptEntity> findByFinancialYearAndDoctorIdentityCharacterAndSerial(
            String financialYear, String doctorIdentityCharacter, Integer serial);

    @Query("SELECT COALESCE(MAX(r.serial), 0) FROM ReciptEntity r WHERE r.financialYear = :financialYear AND r.doctorIdentityCharacter = :doctorIdentityCharacter")
    int findMaxSerialByFinancialYearAndDoctorIdentityCharacter(
            @Param("financialYear") String financialYear,
            @Param("doctorIdentityCharacter") String doctorIdentityCharacter);

    @Query("SELECT DISTINCT r FROM ReciptEntity r JOIN r.payments p JOIN p.treatment t WHERE t.patient.id = :patientId")
    org.springframework.data.domain.Page<ReciptEntity> findByPatientId(@Param("patientId") Long patientId, org.springframework.data.domain.Pageable pageable);
}

