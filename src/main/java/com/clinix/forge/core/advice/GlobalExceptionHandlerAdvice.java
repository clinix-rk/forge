package com.clinix.forge.core.advice;

import com.clinix.forge.core.exception.BaseException;
import com.clinix.forge.core.payload.ApiResponse;
import com.clinix.forge.core.payload.ApiValidationError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandlerAdvice {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleBaseException(BaseException exception) {
        log.warn("Exception Caught [{}] : {}", exception.getStatus(), exception.getMessage());

        return ResponseEntity.status(exception.getStatus())
                .body(ApiResponse.error(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<ApiValidationError> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err ->
                        new ApiValidationError(
                                err.getField(),
                                err.getDefaultMessage(),
                                err.getRejectedValue()
                        ))
                .collect(Collectors.toList());

        log.warn("Validation failed for request. Errors count: {}", validationErrors.size());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Validation failed.", validationErrors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnhandledExceptions(Exception exception) {
        log.error("CRITICAL: Unhandled internal error occurred", exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected system error occurred."));
    }
}
