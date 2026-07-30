package com.clinix.forge.doctors.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Data transfer object for creating a new doctor")
public record CreateDoctorRequest(
        @Size(min = 2, max = 100, message = "Doctor name must be between 2 and 100 characters")
        @Schema(description = "Full name of the doctor", example = "Dr. Rut Koticha")
        String name,

        @NotBlank(message = "Case number prefix is required")
        @Size(max = 5, message = "Case number prefix cannot exceed 5 characters")
        @Schema(description = "Short prefix used for patient case numbers (e.g., R for Rut Koticha)", example = "RK")
        String caseNoPrefix
) {
}