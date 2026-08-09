package com.clinix.forge.catalog.prescription.dosages.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for updating an existing drug dosage pattern")
public record UpdateDrugDosageRequest(
        @NotBlank(message = "Dosage pattern text is required")
        @Schema(description = "The updated dosage prescription pattern (e.g., 1-1-1)", example = "1-1-1")
        String dosage
) {
}
