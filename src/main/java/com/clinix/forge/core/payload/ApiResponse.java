package com.clinix.forge.core.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Standardized, strictly typed, and immutable API Response wrapper.
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        List<ApiValidationError> errors,
        Map<String, Object> meta
) {

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .errors(Collections.emptyList())
                .meta(Map.of("timestamp", Instant.now()))
                .build();
    }

    public static <T> ApiResponse<T> success(String message) {
        return success(message, null);
    }

    public static <T> ApiResponse<T> error(String message, List<ApiValidationError> errors) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .errors(errors != null ? errors : Collections.emptyList())
                .meta(Map.of("timestamp", Instant.now()))
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return error(message, Collections.emptyList());
    }
}