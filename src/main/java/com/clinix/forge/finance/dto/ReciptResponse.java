package com.clinix.forge.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Represents a receipt details (spelled recipt to match database)")
public record ReciptResponse(
        @Schema(description = "Unique ID of the receipt", example = "1")
        Long id,

        @Schema(description = "Single character representing the doctor's identification prefix", example = "A")
        String doctorIdentityCharacter,

        @Schema(description = "The financial year for the receipt", example = "2026-2027")
        String financialYear,

        @Schema(description = "Unique serial number of the receipt", example = "105")
        Integer serial,

        @Schema(description = "Timestamp when the receipt was created")
        Instant createdAt,

        @Schema(description = "Timestamp when the receipt was last updated")
        Instant updatedAt
) {}
