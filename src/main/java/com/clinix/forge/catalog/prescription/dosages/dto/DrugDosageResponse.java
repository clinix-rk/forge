package com.clinix.forge.catalog.prescription.dosages.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Represents a predefined drug dosage pattern details")
public record DrugDosageResponse(
        @Schema(description = "Unique ID of the drug dosage record", example = "1")
        Long id,

        @Schema(description = "The dosage prescription pattern", example = "1-0-1")
        String dosage,

        @Schema(description = "Timestamp when the dosage pattern was created")
        Instant createdAt,

        @Schema(description = "Timestamp when the dosage pattern was last updated")
        Instant updatedAt
) {
}
