package com.clinix.forge.finance;

import com.clinix.forge.finance.entity.PaymentEntity;
import com.clinix.forge.finance.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing PaymentEntity objects.
 */
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findByTreatmentId(Long treatmentId);

    Optional<PaymentEntity> findByPatientId(Long patientId);

    Page<PaymentEntity> findAllByPatientId(Long patientId, Pageable pageable);
}


