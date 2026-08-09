package com.clinix.forge.suggestion.dto;

import com.clinix.forge.suggestion.entity.SuggestionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

@Schema(description = "Request payload for proposing a new suggestion to a patient")
public record CreateSuggestionRequest(
        @NotNull(message = "Date is required")
        @Schema(description = "Date when the suggestion was made")
        LocalDate date,

        @NotNull(message = "Category ID is required")
        @Positive(message = "Category ID must be a positive number")
        @Schema(description = "Unique ID of the associated suggestion category", example = "2")
        Long categoryId,

        @Schema(description = "Detailed explanation of the suggestion", example = "Maintain low sodium diet and daily 30 min walk")
        String details,

        @NotNull(message = "Cost is required")
        @PositiveOrZero(message = "Cost must be zero or a positive number")
        @Schema(description = "Cost associated with this suggestion", example = "500")
        Integer cost,

        @Schema(description = "Status of the suggestion (SUGGESTED, ACCEPTED, DECLINED). Defaults to SUGGESTED if null.")
        SuggestionStatus status,

        @NotNull(message = "Patient ID is required")
        @Positive(message = "Patient ID must be a positive number")
        @Schema(description = "Unique ID of the patient", example = "5")
        Long patientId
) {
}
