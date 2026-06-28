package com.clinix.forge.treatment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for updating a treatment category")
public record UpdateTreatmentCategoryRequest(
        @NotBlank(message = "Category name is required")
        @Size(max = 100, message = "Category name must not exceed 100 characters")
        @Schema(description = "Updated name of the treatment category", example = "Orthodontic Surgery")
        String name,

        @Schema(description = "Updated parent category ID for hierarchical grouping", example = "3")
        Long parentId
) {}
