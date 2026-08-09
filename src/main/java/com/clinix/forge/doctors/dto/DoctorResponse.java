package com.clinix.forge.doctors.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Represents a doctor's profile information within the Clinix system")
public record DoctorResponse(
        @Schema(description = "The database unique identifier for the doctor", example = "1")
        Long id,

        @Schema(description = "Full name of the medical professional", example = "Dr. Rut Koticha")
        String name,

        @Schema(description = "Short prefix used for patient case numbers (e.g., R for Rut Koticha)", example = "RK")
        String caseNoPrefix,

        @Schema(description = "Total number of patients assigned to this doctor", example = "12")
        Integer totalPatients,

        @Schema(description = "Timestamp indicating when the doctor profile was first created")
        Instant createdAt,

        @Schema(description = "Timestamp indicating the last time the doctor profile was modified")
        Instant updatedAt
) {
}
