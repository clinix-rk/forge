package com.clinix.forge.core.exception;

import org.springframework.http.HttpStatus;

public class InsufficientPermissionException extends BaseException {
    public InsufficientPermissionException(String resource) {
        super(
                HttpStatus.FORBIDDEN,
                "INSUFFICIENT_PERMISSION",
                "You do not have permission to access " + resource
        );
    }
}
