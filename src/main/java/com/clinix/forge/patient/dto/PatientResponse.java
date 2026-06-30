package com.clinix.forge.patient.dto;

import com.clinix.forge.patient.entity.Gender;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record PatientResponse(
        @Schema(
                name = "Patient id",
                description = "System id for patient record"
        )
        Long id,

        @Schema(
                name = "Case Number",
                description = "Case number of the patient"
        )
        String caseNo,

        @Schema(
                name = "Full name",
                description = "Full name of the patient"
        )
        String name,

        @Schema(
                name = "Date of Birth",
                description = "Patient's date of birth to mainly calculate age"
        )
        LocalDate dateOfBirth,

        @Schema(
                name = "Gender",
                description = "Patients gender (can be MALE, FEMALE or OTHER)"
        )
        Gender gender,

        @Schema(
                name = "Email",
                description = "Email address of patient"
        )
        String email,

        @Schema(
                name = "Address",
                description = "Patient home address"
        )
        String address,

        @Schema(
                name = "City",
                description = "Residence city"
        )
        String city,

        @Schema(
                name = "Pincode",
                description = "Postal code for the address"
        )
        String pincode,

        @Schema(
                name = "Referred By",
                description = "The doctor whose refrence was brought by the patient"
        )
        String referredBy,

        @Schema(
                name = "Phone Numbers",
                description = "List of Primary and Secondary phone numbers"
        )
        List<PhoneNumberResponse> phoneNumbers,

        @Schema(
                name = "Medical Conditions",
                description = "Patient's medical conditions"
        )
        Set<String> medicalConditions,

        @Schema(
                name = "Drug Allergies",
                description = "Patient's drug allergies"
        )
        Set<String> drugAllergies,

        @Schema(
                name = "Created At",
                description = "The system metadata for when the patient record was created"
        )
        Instant createdAt,

        @Schema(
                name = "Updated At",
                description = "The system metadata for when the patient record was last updated"
        )
        Instant updatedAt
) {}
