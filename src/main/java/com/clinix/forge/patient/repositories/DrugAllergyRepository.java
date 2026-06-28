package com.clinix.forge.patient.repositories;

import com.clinix.forge.patient.entity.DrugAllergyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DrugAllergyRepository extends JpaRepository<DrugAllergyEntity, Long> {
    Optional<DrugAllergyEntity> findByNameIgnoreCase(String name);
}
