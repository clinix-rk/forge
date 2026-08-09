package com.clinix.forge.patient.dto;

import com.clinix.forge.patient.entity.PhoneType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

public record PhoneNumberResponse(
        @Schema(
                description = "System id for phone number"
        )
        Long id,

        @Schema(
                description = "Phone number value"
        )
        String phoneNumber,

        @Schema(
                description = "Type of the phone number. Can be PRIMARY / SECONDARY"
        )
        PhoneType type,

        @Schema(
                description = "The system metadata for when the phone number record was created"
        )
        Instant createdAt,

        @Schema(
                description = "The system metadata for when the phone number record was last updated"
        )
        Instant updatedAt
) {
}
