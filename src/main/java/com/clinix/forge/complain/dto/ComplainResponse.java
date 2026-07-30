package com.clinix.forge.complain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;

@Schema(description = "Represents patient complain details in the system")
public record ComplainResponse(
        @Schema(description = "Unique ID of the complain record", example = "1")
        Long id,

        @Schema(description = "Date when the complain was reported")
        LocalDate date,

        @Schema(description = "Detailed description of the patient's complaint", example = "Patient feels persistent mild chest pain during moderate exercise.")
        String details,

        @Schema(description = "Unique ID of the complain category", example = "3")
        Long categoryId,

        @Schema(description = "Unique ID of the patient", example = "5")
        Long patientId,

        @Schema(description = "Timestamp when the complain record was created")
        Instant createdAt,

        @Schema(description = "Timestamp when the complain record was last updated")
        Instant updatedAt
) {
}
