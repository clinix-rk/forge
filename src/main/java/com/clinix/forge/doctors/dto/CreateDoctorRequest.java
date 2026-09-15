package com.clinix.forge.doctors.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data transfer object representing the payload to create a new doctor entry.
 *
 * <p>Carries user-submitted details required for onboarding a medical professional
 * into the system. Instances of this record are expected to pass validation prior
 * to processing.
 *
 * @param name         the full name of the doctor; must be between 2 and 100 characters
 * @param caseNoPrefix the mandatory, short prefix (up to 5 characters) prepended to generated
 *                     patient case numbers and billing receipts
 */
@Schema(description = "Data transfer object for creating a new doctor")
public record CreateDoctorRequest(
        @NotBlank(message = "Doctor name is required")
        @Size(min = 2, max = 100, message = "Doctor name must be between 2 and 100 characters")
        @Schema(description = "Full name of the doctor", example = "Dr. John Doe")
        String name,

        @NotBlank(message = "Case number prefix is required")
        @Size(max = 5, message = "Case number prefix cannot exceed 5 characters")
        @Schema(
                description = "Short prefix used for patient case numbers (e.g., J for John Doe)",
                example = "J"
        )
        String caseNoPrefix
) {
}
