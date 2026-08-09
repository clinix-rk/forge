package com.clinix.forge.treatment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@Schema(description = "Request payload for registering a new treatment for a patient")
public record CreateTreatmentRequest(
        @Schema(description = "Detailed medical explanation of the treatment performed", example = "Root canal therapy on lower left molar")
        String details,

        @NotNull(message = "Date is required")
        @Schema(description = "Date when the treatment was performed")
        LocalDate date,

        @NotNull(message = "Category ID is required")
        @Positive(message = "Category ID must be a positive number")
        @Schema(description = "Unique ID of the associated treatment category", example = "2")
        Long categoryId,

        @NotNull(message = "Patient ID is required")
        @Positive(message = "Patient ID must be a positive number")
        @Schema(description = "Unique ID of the patient undergoing treatment", example = "5")
        Long patientId
) {
}
