package com.clinix.forge.catalog.prescription.instructions.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for creating a predefined drug dosage pattern")
public record CreateInstructionRequest(
        @NotBlank(message = "Instruction text is required")
        @Schema(description = "The instruction for the drug dosage (e.g., 1-0-1 or once daily)", example = "1-0-1")
        String instruction
) {
}
