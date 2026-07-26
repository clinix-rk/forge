package com.clinix.forge.catalog.medicines.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for updating an existing medicine catalog record")
public record UpdateMedicineRequest(
        @NotBlank(message = "Medicine name is required")
        @Size(max = 100, message = "Medicine name must not exceed 100 characters")
        @Schema(description = "Updated brand or generic name of the medicine", example = "Paracetamol 650mg")
        String name,

        @NotBlank(message = "Medicine type is required")
        @Size(max = 50, message = "Medicine type must not exceed 50 characters")
        @Schema(description = "Updated type/form of the medicine (e.g., Tablet, Capsule, Syrup)", example = "Tablet")
        String type,

        @NotBlank(message = "Instruction is required")
        @Size(max = 255, message = "Instruction must not exceed 255 characters")
        @Schema(description = "Updated administration instructions for this medicine", example = "Take after food with warm water")
        String instruction
) {}
