package com.clinix.forge.complain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for creating a complain category")
public record CreateComplainCategoryRequest(
        @NotBlank(message = "Category name is required")
        @Size(max = 100, message = "Category name must not exceed 100 characters")
        @Schema(description = "Name of the complain category", example = "Cardiovascular issues")
        String name,

        @Schema(description = "Optional parent category ID for hierarchical grouping", example = "2")
        Long parentId
) {}
