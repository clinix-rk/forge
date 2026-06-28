package com.clinix.forge.prescription.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Request payload representing a medicine item, its dosage and quantity within a prescription")
public record PrescriptionMedicineRequest(
        @NotNull(message = "Medicine ID is required")
        @Positive(message = "Medicine ID must be a positive number")
        @Schema(description = "Unique ID of the medicine to prescribe", example = "1")
        Long medicineId,

        @NotNull(message = "Dosage ID is required")
        @Positive(message = "Dosage ID must be a positive number")
        @Schema(description = "Unique ID of the dosage pattern", example = "2")
        Long dosageId,

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be a positive number")
        @Schema(description = "Quantity of medicine units to prescribe", example = "10")
        Integer quantity
) {}
