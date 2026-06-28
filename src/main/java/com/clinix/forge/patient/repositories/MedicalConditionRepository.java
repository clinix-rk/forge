package com.clinix.forge.patient.repositories;

import com.clinix.forge.patient.entity.MedicalConditionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicalConditionRepository extends JpaRepository<MedicalConditionEntity, Long> {
    Optional<MedicalConditionEntity> findByNameIgnoreCase(String name);
}
