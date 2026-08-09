package com.clinix.forge.prescription.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Represents a medicine item details linked within a prescription")
public record PrescriptionMedicineResponse(
        @Schema(description = "Unique ID of the prescription medicine item record", example = "1")
        Long id,

        @Schema(description = "Unique ID of the medicine catalog item", example = "2")
        Long medicineId,

        @Schema(description = "Unique ID of the drug dosage pattern", example = "3")
        Long dosageId,

        @Schema(description = "Unique ID of the instruction", example = "3")
        Long instructionId,

        @Schema(description = "Quantity of medicine units prescribed", example = "10")
        Integer quantity,

        @Schema(description = "Timestamp when the prescription medicine record was created")
        Instant createdAt,

        @Schema(description = "Timestamp when the prescription medicine record was last updated")
        Instant updatedAt
) {
}
