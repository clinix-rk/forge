package com.clinix.forge.suggestion.dto;

import com.clinix.forge.suggestion.entity.SuggestionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Schema(description = "Request payload for proposing a new suggestion to a patient")
public record CreateSuggestionRequest(
        @NotNull(message = "Date is required")
        @Schema(description = "Date when the suggestion was made")
        LocalDate date,

        @NotBlank(message = "Category is required")
        @Size(max = 100, message = "Category must not exceed 100 characters")
        @Schema(description = "Category of the suggestion (e.g., Diet, Exercise, Surgery)", example = "Diet Plan")
        String category,

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
) {}
