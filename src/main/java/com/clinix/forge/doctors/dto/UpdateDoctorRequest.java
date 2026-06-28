package com.clinix.forge.doctors.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for updating an existing doctor's information")
public record UpdateDoctorRequest(

        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        @Schema(description = "Updated name for the doctor", example = "Dr. Rut Koticha")
        String name
) {}

