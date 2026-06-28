package com.clinix.forge.complain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for updating a complain category")
public record UpdateComplainCategoryRequest(
        @NotBlank(message = "Category name is required")
        @Size(max = 100, message = "Category name must not exceed 100 characters")
        @Schema(description = "Updated name of the complain category", example = "Hypertension and Cardiac")
        String name,

        @Schema(description = "Updated parent category ID for hierarchical grouping", example = "2")
        Long parentId
) {}
