package com.clinix.forge.complain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

@Schema(description = "Request payload for updating a patient complain")
public record UpdateComplainRequest(
        @NotNull(message = "Date is required")
        @Schema(description = "Date when the complain was reported")
        LocalDate date,

        @Schema(description = "Detailed description of the patient's complaint", example = "Patient feels persistent mild chest pain during moderate exercise.")
        String details,

        @NotNull(message = "Category ID is required")
        @Positive(message = "Category ID must be a positive number")
        @Schema(description = "Unique ID of the complain category", example = "1")
        Long categoryId
) {}
