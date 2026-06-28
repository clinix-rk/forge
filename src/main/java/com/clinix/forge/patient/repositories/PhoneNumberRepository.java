package com.clinix.forge.patient.repositories;

import com.clinix.forge.patient.entity.PhoneNumberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneNumberRepository extends JpaRepository<PhoneNumberEntity, Long> {
}
