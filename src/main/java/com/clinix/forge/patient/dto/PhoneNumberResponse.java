package com.clinix.forge.patient.dto;

import com.clinix.forge.patient.entity.PhoneType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Data transfer object to respond with patient's phone number
 *
 * @param id          System id for phone number
 * @param phoneNumber patient's phone number
 * @param type        type of the phone number
 * @param createdAt   timestamp of the creation of the record
 * @param updatedAt   timestamp of the last update to the record
 *
 */
@Schema(description = "Data transfer object to respond with patient's phone number")
public record PhoneNumberResponse(
        @Schema(
                description = "System id for phone number",
                example = "1"
        )
        Long id,

        @Schema(
                description = "Phone number of the patient",
                example = "1234567891"
        )
        String phoneNumber,

        @Schema(
                description = "Type of the phone number. Can be PRIMARY / SECONDARY",
                example = "PRIMARY"
        )
        PhoneType type,

        @Schema(
                description = "Timestamp at which the phone number record was created."
        )
        Instant createdAt,

        @Schema(
                description = "Timestamp at which the phone number record was last updated."
        )
        Instant updatedAt
) {
}
