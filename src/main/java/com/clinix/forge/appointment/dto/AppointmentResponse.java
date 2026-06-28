package com.clinix.forge.appointment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.time.LocalDateTime;

@Schema(description = "Represents appointment details within the Clinix system")
public record AppointmentResponse(
        @Schema(description = "Unique identifier of the appointment", example = "1")
        Long id,

        @Schema(description = "Unique identifier of the patient", example = "5")
        Long patientId,

        @Schema(description = "Clinical or general notes recorded for the appointment", example = "Follow-up checkup for blood pressure monitoring")
        String notes,

        @Schema(description = "Date and time scheduled for the appointment")
        LocalDateTime datetime,

        @Schema(description = "Timestamp when the appointment record was created")
        Instant createdAt,

        @Schema(description = "Timestamp when the appointment record was last updated")
        Instant updatedAt
) {}
