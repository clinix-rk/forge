package com.clinix.forge.prescription.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "Request payload for creating a patient prescription")
public record CreatePrescriptionRequest(
        @NotNull(message = "Patient ID is required")
        @Positive(message = "Patient ID must be a positive number")
        @Schema(description = "Unique ID of the patient", example = "5")
        Long patientId,

        @NotNull(message = "Date is required")
        @Schema(description = "Date when the prescription was written")
        LocalDate date,

        @NotBlank(message = "Details cannot be blank")
        @Schema(description = "Detailed clinical observations, notes, or comments", example = "Patient shows symptoms of seasonal allergies. Rest advised.")
        String details,

        @Valid
        @Schema(description = "List of medicines and their dosage details prescribed")
        List<PrescriptionMedicineRequest> medicines
) {}
