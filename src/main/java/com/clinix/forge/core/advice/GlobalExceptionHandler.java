package com.clinix.forge.core.advice;

import com.clinix.forge.core.exception.BaseException;
import com.clinix.forge.core.payload.ApiError;
import com.clinix.forge.core.payload.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String MDC_TRACE_ID_KEY = "traceId";

    /**
     * Handle custom business/validation exceptions (4xx range)
     * Examples: UserNotFoundException, InvalidCredentialsException
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleBaseException(BaseException exception) {
        // Log at WARN level for 4xx (client errors)
        if (exception.getStatus().is4xxClientError()) {
            log.warn(
                    "Client error [{}] | Code: {} | Message: {}",
                    exception.getStatus().value(),
                    exception.getErrorCode(),
                    exception.getMessage()
            );
        } else {
            // Log at ERROR for 5xx (server errors)
            log.error(
                    "Server error [{}] | Code: {} | Message: {}",
                    exception.getStatus().value(),
                    exception.getErrorCode(),
                    exception.getMessage(),
                    exception
            );
        }

        return ResponseEntity.status(exception.getStatus())
                .body(ApiResponse.error(
                        exception.getStatus().value(),
                        exception.getErrorCode(),
                        exception.getMessage()
                ));
    }

    /**
     * Handle Spring validation errors (400 Bad Request)
     * Triggered by @Valid on request body
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException ex
    ) {
        // Extract field-level validation errors
        List<ApiError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ApiError(
                        fe.getField(),
                        fe.getCode() != null ? fe.getCode() : "VALIDATION_ERROR",
                        fe.getDefaultMessage()
                ))
                .toList();

        // Log validation failures (WARNING level - client error)
        log.warn(
                "Validation failed | Field count: {} | Errors: {}",
                errors.size(),
                errors.stream()
                        .map(e -> String.format("%s:%s", e.field(), e.code()))
                        .toList()
        );

        return ResponseEntity.badRequest().body(
                ApiResponse.validationError(errors)
        );
    }

    /**
     * Handle all unhandled exceptions (500 Internal Server Error)
     * Fallback handler for unexpected errors
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        // Log at ERROR level with full stack trace
        log.error(
                "Unhandled exception occurred | Type: {} | Message: {}",
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                ex // This parameter logs the full exception stack trace
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.serverError());
    }

    /**
     * Handle IllegalArgumentException separately (400 vs 500)
     * Client passed invalid argument structure
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
            IllegalArgumentException ex
    ) {
        log.warn(
                "Illegal argument provided | Message: {}",
                ex.getMessage()
        );

        return ResponseEntity.badRequest().body(
                ApiResponse.error(
                        400,
                        "INVALID_ARGUMENT",
                        ex.getMessage()
                ));
    }

    /**
     * Handle resource not found errors (404)
     * More specific than generic BaseException handler
     */
    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFound(
            org.springframework.web.servlet.resource.NoResourceFoundException ex
    ) {
        log.warn(
                "Resource not found | Path: {} | Method: {}",
                ex.getResourcePath(),
                ex.getHttpMethod()
        );

        return ResponseEntity.notFound().build(); // Simple 404 without ApiResponse
    }

    /**
     * Handle max upload size exceeded errors (400 Bad Request)
     */
    @ExceptionHandler(org.springframework.web.multipart.MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxUploadSizeExceeded(
            org.springframework.web.multipart.MaxUploadSizeExceededException ex
    ) {
        log.warn("Uploaded file exceeds maximum allowed size: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(
                ApiResponse.error(
                        400,
                        "FILE_TOO_LARGE",
                        "File size exceeds limit of 10 MB."
                )
        );
    }
}
