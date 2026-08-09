package com.clinix.forge.catalog.prescription.instructions.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for updating an existing drug dosage pattern")
public record UpdateInstructionRequest(
        @NotBlank(message = "Instruction text is required")
        @Schema(description = "The updated instruction for the drug dosage", example = "1-1-1")
        String instruction
) {
}
