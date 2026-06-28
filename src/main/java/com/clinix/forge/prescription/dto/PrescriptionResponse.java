package com.clinix.forge.prescription.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "Represents patient prescription details in the system")
public record PrescriptionResponse(
        @Schema(description = "Unique ID of the prescription", example = "1")
        Long id,

        @Schema(description = "Unique ID of the patient", example = "5")
        Long patientId,

        @Schema(description = "Date when the prescription was written")
        LocalDate date,

        @Schema(description = "Detailed clinical observations, notes, or comments", example = "Patient shows symptoms of seasonal allergies. Rest advised.")
        String details,

        @Schema(description = "List of medicines and their dosage details prescribed")
        List<PrescriptionMedicineResponse> medicines,

        @Schema(description = "Timestamp when the prescription was created")
        Instant createdAt,

        @Schema(description = "Timestamp when the prescription was last updated")
        Instant updatedAt
) {}
