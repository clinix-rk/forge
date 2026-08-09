package com.clinix.forge.core.payload;

import io.swagger.v3.oas.annotations.media.Schema;

public record ApiError(
        @Schema(
                description = "Indicates the field which is having validation error",
                example = "Name"
        )
        String field,

        @Schema(
                description = "System code for the violation",
                example = "NAME_STRING_INVALID"
        )
        String code,

        @Schema(
                description = "Human readable message for the error",
                example = "Name can not contain @ symbol."
        )
        String message
) {
    public ApiError(String code, String message) {
        this(null, code, message);
    }
}
