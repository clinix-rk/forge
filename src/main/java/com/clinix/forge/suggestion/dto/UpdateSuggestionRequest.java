package com.clinix.forge.suggestion.dto;

import com.clinix.forge.suggestion.entity.SuggestionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

@Schema(description = "Request payload for updating an existing suggestion's details")
public record UpdateSuggestionRequest(
        @NotNull(message = "Date is required")
        @Schema(description = "Updated date when the suggestion was made")
        LocalDate date,

        @NotNull(message = "Category ID is required")
        @Positive(message = "Category ID must be a positive number")
        @Schema(description = "Updated unique ID of the associated treatment category", example = "2")
        Long categoryId,

        @Schema(description = "Updated detailed explanation of the suggestion", example = "Maintain low sodium diet and daily 30 min walk")
        String details,

        @NotNull(message = "Cost is required")
        @PositiveOrZero(message = "Cost must be zero or a positive number")
        @Schema(description = "Updated cost associated with this suggestion", example = "500")
        Integer cost,

        @NotNull(message = "Status is required")
        @Schema(description = "Updated status of the suggestion (SUGGESTED, ACCEPTED, DECLINED)")
        SuggestionStatus status
) {
}
