package com.clinix.forge.appointment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

@Schema(description = "Request payload for creating a new appointment")
public record CreateAppointmentRequest(
        @NotNull(message = "Patient ID is required")
        @Positive(message = "Patient ID must be a positive number")
        @Schema(description = "Unique ID of the patient", example = "1")
        Long patientId,

        @Schema(description = "Optional clinical or general notes for the appointment", example = "Follow-up checkup for blood pressure monitoring")
        String notes,

        @NotNull(message = "Appointment date and time is required")
        @Schema(description = "Date and time scheduled for the appointment")
        LocalDateTime datetime
) {}
