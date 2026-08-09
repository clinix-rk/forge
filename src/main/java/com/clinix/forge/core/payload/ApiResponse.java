package com.clinix.forge.core.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        @Schema(
                description = "Boolean value describing whether the response is success or failure",
                example = "true"
        )
        boolean success,

        @Schema(
                description = "HTTP status code returned with the response",
                example = "200"
        )
        int statusCode,

        @Schema(
                description = "Human readable message",
                example = "User login successful"
        )
        String message,

        @Schema(description = "Actual data that is being returned")
        T data,

        @Schema(
                description = "In case there are any errors they are going to be listed here"
        )
        List<ApiError> errors,

        @Schema(description = "Pagination metadata if the respone is of page")
        PaginationMetadata pagination,

        @Schema(description = "Timestamp of the return of the response")
        Instant timestamp
) {
    // Non-paginated success
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
                true,
                200,
                "Request successful",
                data,
                null,
                null,
                Instant.now()
        );
    }

    // Paginated success
    public static <T> ApiResponse<T> success(T data, PaginationMetadata pagination) {
        return new ApiResponse<>(
                true,
                200,
                "Request successful",
                data,
                null,
                pagination,
                Instant.now()
        );
    }

    // Validation errors (400)
    public static <T> ApiResponse<T> validationError(List<ApiError> errors) {
        return new ApiResponse<>(
                false,
                400,
                "Validation failed",
                null,
                errors,
                null,
                Instant.now()
        );
    }

    // Business/domain error (4xx range)
    public static <T> ApiResponse<T> error(
            int statusCode,
            String code,
            String message
    ) {
        return new ApiResponse<>(
                false,
                statusCode,
                message,
                null,
                List.of(new ApiError(code, message)),
                null,
                Instant.now()
        );
    }

    // Server error (500)
    public static <T> ApiResponse<T> serverError() {
        return new ApiResponse<>(
                false,
                500,
                "Internal server error",
                null,
                List.of(new ApiError("SERVER_ERROR", "An unexpected error occurred")),
                null,
                Instant.now()
        );
    }
}
