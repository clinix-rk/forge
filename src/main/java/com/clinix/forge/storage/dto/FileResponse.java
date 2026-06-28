package com.clinix.forge.storage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Represents patient file metadata details in the system")
public record FileResponse(
        @Schema(description = "Unique ID of the file record", example = "1")
        Long id,

        @Schema(description = "Unique ID of the patient to whom this file belongs", example = "5")
        Long patientId,

        @Schema(description = "Display name of the file", example = "X-Ray_Chest_2026.png")
        String name,

        @Schema(description = "Storage path or URL of the file", example = "/uploads/patients/5/xray.png")
        String location,

        @Schema(description = "Timestamp when the file was registered")
        Instant createdAt,

        @Schema(description = "Timestamp when the file metadata was last updated")
        Instant updatedAt
) {}
