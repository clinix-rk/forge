package com.clinix.forge.doctors.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Data transfer object representing a doctor's profile within the Clinix system.
 *
 * <p>Contains system-generated metadata alongside domain-specific identifier prefixes
 * used across billing and patient record workflows.
 *
 * @param id           the unique database identifier for the doctor
 * @param name         the full name of the doctor
 * @param caseNoPrefix the alphabetical prefix appended to patient case files and receipt identifiers
 * @param createdAt    the UTC timestamp marking when the doctor record was created
 * @param updatedAt    the UTC timestamp marking the most recent modification of the doctor record
 */
@Schema(description = "Represents a doctor's profile information within the Clinix system")
public record DoctorResponse(
        @Schema(description = "The database unique identifier for the doctor", example = "1")
        Long id,

        @Schema(description = "Full name of the doctor", example = "Dr. John Doe")
        String name,

        @Schema(
                description = "Short prefix used for patient case numbers (e.g., J for John Doe)",
                example = "J"
        )
        String caseNoPrefix,

        @Schema(description = "Timestamp indicating when the doctor record was first created")
        Instant createdAt,

        @Schema(description = "Timestamp indicating the last time the doctor record was modified")
        Instant updatedAt
) {
}
