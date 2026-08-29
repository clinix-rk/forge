package com.clinix.forge.doctors.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * Data transfer object representing the payload to update an existing doctor entry.
 *
 * <p>Carries user-submitted details for modifying a doctor's profile. Fields set to
 * {@code null} indicate that the corresponding property should remain unchanged during
 * partial update operations.
 *
 * @param name the updated full name of the doctor;
 *             if provided, must be between 2 and 100 characters
 */
@Schema(description = "Request payload for updating an existing doctor's information")
public record UpdateDoctorRequest(
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        @Schema(description = "Updated name for the doctor", example = "Dr. John Doe")
        String name
) {
}
