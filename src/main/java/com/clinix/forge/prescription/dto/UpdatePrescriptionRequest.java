package com.clinix.forge.prescription.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "Request payload for updating an existing prescription")
public record UpdatePrescriptionRequest(
        @NotNull(message = "Date is required")
        @Schema(description = "Updated date when the prescription was written")
        LocalDate date,

        @NotBlank(message = "Details cannot be blank")
        @Schema(description = "Updated detailed clinical observations, notes, or comments", example = "Patient shows symptoms of seasonal allergies. Rest advised.")
        String details,

        @Valid
        @Schema(description = "Updated list of medicines and their dosage details prescribed")
        List<PrescriptionMedicineRequest> medicines
) {}
