package com.clinix.forge.core.exception;

import org.springframework.http.HttpStatus;

/**
 * Base exception for all business logic exceptions
 * Extends RuntimeException for unchecked exception handling
 */
public class BaseException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    public BaseException(HttpStatus status, String errorCode, String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public BaseException(HttpStatus status, String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
