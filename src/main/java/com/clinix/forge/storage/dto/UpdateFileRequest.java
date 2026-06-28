package com.clinix.forge.storage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for updating an uploaded file's metadata")
public record UpdateFileRequest(
        @NotBlank(message = "File name is required")
        @Size(max = 255, message = "File name must not exceed 255 characters")
        @Schema(description = "Updated display name of the file", example = "X-Ray_Chest_2026_updated.png")
        String name,

        @NotBlank(message = "File location is required")
        @Size(max = 512, message = "File location must not exceed 512 characters")
        @Schema(description = "Updated storage path or URL of the file", example = "/uploads/patients/5/xray_v2.png")
        String location
) {}
