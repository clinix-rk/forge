package com.clinix.forge.core.exception;

import org.springframework.http.HttpStatus;

public class AccessDeniedException extends BaseException {

    public AccessDeniedException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
