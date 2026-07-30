package com.clinix.forge.suggestion.dto;

import com.clinix.forge.suggestion.entity.SuggestionStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;

@Schema(description = "Represents details of a proposed suggestion in the system")
public record SuggestionResponse(
        @Schema(description = "Unique ID of the suggestion record", example = "1")
        Long id,

        @Schema(description = "Date when the suggestion was made")
        LocalDate date,

        @Schema(description = "Category of the suggestion", example = "Diet Plan")
        String category,

        @Schema(description = "Detailed explanation of the suggestion", example = "Maintain low sodium diet and daily 30 min walk")
        String details,

        @Schema(description = "Cost associated with this suggestion", example = "500")
        Integer cost,

        @Schema(description = "Status of the suggestion (SUGGESTED, ACCEPTED, DECLINED)")
        SuggestionStatus status,

        @Schema(description = "Unique ID of the patient", example = "5")
        Long patientId,

        @Schema(description = "Timestamp when the suggestion record was created")
        Instant createdAt,

        @Schema(description = "Timestamp when the suggestion record was last updated")
        Instant updatedAt
) {
}
