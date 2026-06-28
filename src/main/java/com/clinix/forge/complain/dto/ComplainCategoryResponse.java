package com.clinix.forge.complain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Represents a category of patient complaints in the system")
public record ComplainCategoryResponse(
        @Schema(description = "Unique ID of the complain category", example = "1")
        Long id,

        @Schema(description = "Name of the complain category", example = "Cardiovascular issues")
        String name,

        @Schema(description = "Parent category ID if this is a subcategory", example = "2")
        Long parentId,

        @Schema(description = "Timestamp when the category was created")
        Instant createdAt,

        @Schema(description = "Timestamp when the category was last updated")
        Instant updatedAt
) {}
