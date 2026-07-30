package com.clinix.forge.treatment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@Schema(description = "Request payload for updating an existing treatment's details")
public record UpdateTreatmentRequest(
        @Schema(description = "Updated detailed medical explanation of the treatment performed", example = "Root canal therapy on lower left molar with crown placement")
        String details,

        @NotNull(message = "Date is required")
        @Schema(description = "Updated date when the treatment was performed")
        LocalDate date,

        @NotNull(message = "Category ID is required")
        @Positive(message = "Category ID must be a positive number")
        @Schema(description = "Updated unique ID of the associated treatment category", example = "2")
        Long categoryId
) {
}
