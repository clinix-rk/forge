package com.clinix.forge.patient.dto;

import com.clinix.forge.patient.entity.Gender;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record PatientResponse(
        @Schema(
                description = "System id for patient record"
        )
        Long id,

        @Schema(
                description = "Case number of the patient"
        )
        String caseNo,

        @Schema(
                description = "Full name of the patient"
        )
        String name,

        @Schema(
                description = "Patient's date of birth to mainly calculate age"
        )
        LocalDate dateOfBirth,

        @Schema(
                description = "Patients gender (can be MALE, FEMALE or OTHER)"
        )
        Gender gender,

        @Schema(
                name = "Email",
                description = "Email address of patient"
        )
        String email,

        @Schema(
                description = "Patient home address"
        )
        String address,

        @Schema(
                description = "Residence city"
        )
        String city,

        @Schema(
                description = "Postal code for the address"
        )
        String pincode,

        @Schema(
                description = "The doctor whose refrence was brought by the patient"
        )
        String referredBy,

        @Schema(
                description = "List of Primary and Secondary phone numbers"
        )
        List<PhoneNumberResponse> phoneNumbers,

        @Schema(
                description = "Patient's medical conditions"
        )
        Set<String> medicalConditions,

        @Schema(
                description = "Patient's drug allergies"
        )
        Set<String> drugAllergies,

        @Schema(
                description = "The system metadata for when the patient record was created"
        )
        Instant createdAt,

        @Schema(
                description = "The system metadata for when the patient record was last updated"
        )
        Instant updatedAt
) {}
