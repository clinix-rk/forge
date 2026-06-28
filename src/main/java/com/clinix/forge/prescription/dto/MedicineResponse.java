package com.clinix.forge.prescription.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Represents details of a medicine in the system catalog")
public record MedicineResponse(
        @Schema(description = "Unique ID of the medicine catalog item", example = "1")
        Long id,

        @Schema(description = "Name of the medicine", example = "Paracetamol 500mg")
        String name,

        @Schema(description = "Type/form of the medicine", example = "Tablet")
        String type,

        @Schema(description = "Administration instructions", example = "Take after food with warm water")
        String instruction,

        @Schema(description = "Timestamp when the medicine was added to catalog")
        Instant createdAt,

        @Schema(description = "Timestamp when the medicine was last updated")
        Instant updatedAt
) {}
