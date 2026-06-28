package com.clinix.forge.patient.dto;

import com.clinix.forge.patient.entity.PhoneType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PhoneNumberRequest(
        @NotBlank(message = "Phone number value cannot be blank")
        @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$", message = "Phone number must be in E.164 format, e.g., +1234567890")
        String phoneNumber,

        @NotNull(message = "Phone type (PRIMARY/SECONDARY) must be specified")
        PhoneType type
) {}

