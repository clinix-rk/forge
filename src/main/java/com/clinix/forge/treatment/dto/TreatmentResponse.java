package com.clinix.forge.treatment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;

@Schema(description = "Represents treatment details in the system")
public record TreatmentResponse(
        @Schema(description = "Unique ID of the treatment record", example = "1")
        Long id,

        @Schema(description = "Detailed medical explanation of the treatment performed", example = "Root canal therapy on lower left molar")
        String details,

        @Schema(description = "Date when the treatment was performed")
        LocalDate date,

        @Schema(description = "Unique ID of the associated treatment category", example = "2")
        Long categoryId,

        @Schema(description = "Unique ID of the patient undergoing treatment", example = "5")
        Long patientId,

        @Schema(description = "Timestamp when the treatment record was created")
        Instant createdAt,

        @Schema(description = "Timestamp when the treatment record was last updated")
        Instant updatedAt
) {
}
