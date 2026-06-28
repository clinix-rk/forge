package com.clinix.forge.patient.dto;

import com.clinix.forge.patient.entity.Gender;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record PatientResponse(
        Long id,
        String caseNo,
        String name,
        LocalDate dateOfBirth,
        Gender gender,
        String email,
        String address,
        String city,
        String pincode,
        String referredBy,
        List<PhoneNumberResponse> phoneNumbers,
        Set<String> medicalConditions,
        Set<String> drugAllergies,
        Instant createdAt,
        Instant updatedAt
) {}
