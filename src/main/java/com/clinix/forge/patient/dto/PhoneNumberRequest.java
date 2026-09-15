package com.clinix.forge.patient.dto;

import com.clinix.forge.patient.entity.PhoneType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Data transfer object for patient's phone numbers
 *
 * @param phoneNumber phone number of the patient
 * @param type        type of the phone numbers; { PRIMARY, SECONDARY }
 */
@Schema(description = "Data transfer object for patient's phone numbers")
public record PhoneNumberRequest(
        @NotBlank(message = "Phone number value cannot be blank")
        @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$", message = "Phone number must be in E.164 format, e.g., +1234567890")
        @Schema(description = "Valid phone number of the patient", example = "1234567891")
        String phoneNumber,

        @NotNull(message = "Phone type (PRIMARY/SECONDARY) must be specified")
        @Schema(description = "Type of the phone number", example = "PRIMARY")
        PhoneType type
) {
}
