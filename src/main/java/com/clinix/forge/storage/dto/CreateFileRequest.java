package com.clinix.forge.storage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for uploading or registering a patient file record")
public record CreateFileRequest(
        @NotNull(message = "Patient ID is required")
        @Positive(message = "Patient ID must be a positive number")
        @Schema(description = "Unique ID of the patient to whom this file belongs", example = "5")
        Long patientId,

        @NotBlank(message = "File name is required")
        @Size(max = 255, message = "File name must not exceed 255 characters")
        @Schema(description = "Display name of the file", example = "X-Ray_Chest_2026.png")
        String name,

        @NotBlank(message = "File location is required")
        @Size(max = 512, message = "File location must not exceed 512 characters")
        @Schema(description = "Storage path or URL of the file", example = "/uploads/patients/5/xray.png")
        String location
) {
}
