package com.clinix.forge.appointment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Schema(description = "Request payload for updating an existing appointment's details")
public record UpdateAppointmentRequest(
        @Schema(description = "Updated clinical or general notes for the appointment", example = "Rescheduled follow-up checkup")
        String notes,

        @NotNull(message = "Appointment date and time is required")
        @Schema(description = "Updated date and time scheduled for the appointment")
        LocalDateTime datetime
) {}
