package com.clinix.forge.core.payload;

import io.swagger.v3.oas.annotations.media.Schema;

public record PaginationMetadata(
        @Schema(
                description = "Page number of the current page, 0 Indexed",
                example = "1"
        )
        int page,

        @Schema(
                description = "Size of the current page",
                example = "20"
        )
        int pageSize,

        @Schema(
                description = "Total elements among all the pages",
                example = "100"
        )
        long totalElements,

        @Schema(
                description = "Total pages available",
                example = "10"
        )
        int totalPages,

        @Schema(
                description = "Boolean describing whether there is a next page or not",
                example = "true"
        )
        boolean hasNext,

        @Schema(
                description = "Boolean describing whether there is a previous page or not",
                example = "true"
        )
        boolean hasPrevious
) {
}
