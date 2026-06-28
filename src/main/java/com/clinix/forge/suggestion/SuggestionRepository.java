package com.clinix.forge.suggestion;

import com.clinix.forge.suggestion.entity.SuggestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing SuggestionEntity objects.
 */
public interface SuggestionRepository extends JpaRepository<SuggestionEntity, Long> {
    org.springframework.data.domain.Page<SuggestionEntity> findByPatientId(Long patientId, org.springframework.data.domain.Pageable pageable);
}
