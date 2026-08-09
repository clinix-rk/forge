package com.clinix.forge.catalog.treatments.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for creating a treatment category")
public record CreateTreatmentCategoryRequest(
        @NotBlank(message = "Category name is required")
        @Size(max = 100, message = "Category name must not exceed 100 characters")
        @Schema(description = "Name of the treatment category", example = "Dental Procedures")
        String name,

        @Schema(description = "Optional parent category ID for hierarchical grouping", example = "3")
        Long parentId
) {
}
