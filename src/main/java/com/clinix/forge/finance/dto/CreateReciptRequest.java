package com.clinix.forge.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for creating a receipt (spelled recipt to match database)")
public record CreateReciptRequest(
        @NotBlank(message = "Doctor identity character is required")
        @Size(min = 1, max = 1, message = "Doctor identity character must be exactly 1 character")
        @Schema(description = "Single character representing the doctor's identification prefix", example = "A")
        String doctorIdentityCharacter,

        @NotBlank(message = "Financial year is required")
        @Size(max = 50, message = "Financial year must not exceed 50 characters")
        @Schema(description = "The financial year for the receipt", example = "2026-2027")
        String financialYear,

        @NotNull(message = "Serial number is required")
        @Positive(message = "Serial number must be a positive number")
        @Schema(description = "Unique serial number of the receipt", example = "105")
        Integer serial
) {}
