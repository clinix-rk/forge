package com.clinix.forge.finance;

import com.clinix.forge.finance.entity.PaymentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing PaymentEntity objects.
 */
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findByTreatmentId(Long treatmentId);

    Optional<PaymentEntity> findByPatientId(Long patientId);

    Page<PaymentEntity> findAllByPatientId(Long patientId, Pageable pageable);
}


