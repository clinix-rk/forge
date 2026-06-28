package com.clinix.forge.complain.repository;

import com.clinix.forge.complain.entity.ComplainEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing ComplainEntity objects.
 */
public interface ComplainRepository extends JpaRepository<ComplainEntity, Long> {
    org.springframework.data.domain.Page<ComplainEntity> findByPatientId(Long patientId, org.springframework.data.domain.Pageable pageable);
}

