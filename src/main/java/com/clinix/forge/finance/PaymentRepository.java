package com.clinix.forge.finance.mapper;

import com.clinix.forge.finance.entity.PaymentEntity;
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
    List<PaymentEntity> findAllByReciptId(Long reciptId);
    Optional<PaymentEntity> findByTreatmentId(Long treatmentId);

    @org.springframework.data.jpa.repository.Query("SELECT p FROM PaymentEntity p WHERE p.treatment.patient.id = :patientId")
    org.springframework.data.domain.Page<PaymentEntity> findByPatientId(@org.springframework.data.repository.query.Param("patientId") Long patientId, org.springframework.data.domain.Pageable pageable);

    @Query("""
        SELECT p FROM PaymentEntity p
        JOIN FETCH p.treatment t
        JOIN FETCH t.patient pat
        JOIN FETCH p.recipt r
        WHERE (:method IS NULL OR p.method = :method)
          AND (cast(:fromDate as localdate) IS NULL OR t.date >= :fromDate)
          AND (cast(:toDate as localdate) IS NULL OR t.date <= :toDate)
          AND (:search IS NULL OR 
               LOWER(pat.name) LIKE LOWER(CONCAT('%', :search, '%')) OR 
               LOWER(pat.caseNo) LIKE LOWER(CONCAT('%', :search, '%')) OR 
               LOWER(p.reference) LIKE LOWER(CONCAT('%', :search, '%')))
    """)
    Page<PaymentEntity> findEnrichedPayments(
        @Param("method") com.clinix.forge.finance.entity.PaymentMethod method,
        @Param("fromDate") LocalDate fromDate,
        @Param("toDate") LocalDate toDate,
        @Param("search") String search,
        Pageable pageable
    );
}


