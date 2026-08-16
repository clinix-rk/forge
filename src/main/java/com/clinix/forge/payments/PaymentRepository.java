package com.clinix.forge.payments;

import com.clinix.forge.payments.entity.PaymentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repository interface for managing PaymentEntity objects.
 */
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long>, JpaSpecificationExecutor<PaymentEntity> {
    Optional<PaymentEntity> findByTreatmentId(Long treatmentId);

    Optional<PaymentEntity> findByPatientId(Long patientId);

    Page<PaymentEntity> findAllByPatientId(Long patientId, Pageable pageable);

    @Query("""
            SELECT MAX(p.serial)
            FROM PaymentEntity p
            WHERE p.financialYear = :financialYear
                        AND p.doctorIdentityCharacter = :doctorIdentityCharacter
            """)
    Optional<Integer> findMaxSerialByFinancialYearAndDoctorIdentityCharacter(
            @Param("financialYear") String financialYear,
            @Param("doctorIdentityCharacter") String doctorIdentityCharacter
    );
}
