package com.clinix.forge.core.payload;

/**
 * Standardized structure for field-level validation errors or business logic errors.
 */
public record ApiValidationError(
        String field,
        String message,
        Object rejectedValue
) {}