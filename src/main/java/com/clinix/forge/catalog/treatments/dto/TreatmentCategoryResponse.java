package com.clinix.forge.catalog.treatments.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Represents a treatment category details in the system")
public record TreatmentCategoryResponse(
        @Schema(description = "Unique ID of the treatment category", example = "1")
        Long id,

        @Schema(description = "Name of the treatment category", example = "Dental Procedures")
        String name,

        @Schema(description = "Parent category ID if this is a subcategory", example = "3")
        Long parentId,

        @Schema(description = "Timestamp when the category was created")
        Instant createdAt,

        @Schema(description = "Timestamp when the category was last updated")
        Instant updatedAt
) {}
