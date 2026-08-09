package com.clinix.forge.catalog.prescription.dosages.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for creating a predefined drug dosage pattern")
public record CreateDrugDosageRequest(
        @NotBlank(message = "Dosage pattern text is required")
        @Schema(description = "The dosage prescription pattern (e.g., 1-0-1 or once daily)", example = "1-0-1")
        String dosage
) {
}
