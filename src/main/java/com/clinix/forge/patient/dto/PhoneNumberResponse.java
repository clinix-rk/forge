package com.clinix.forge.patient.dto;

import com.clinix.forge.patient.entity.PhoneType;

import java.time.Instant;

public record PhoneNumberResponse(
        Long id,
        String phoneNumber,
        PhoneType type,
        Instant createdAt,
        Instant updatedAt
) {}
