package com.clinix.forge.core.exception;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends BaseException {

    public DuplicateResourceException(String resource, String value) {
        super(
                HttpStatus.CONFLICT,
                "DUPLICATE_RESOURCE",
                resource + " with value '" + value + "' already exists"
        );
    }

    public DuplicateResourceException(String message) {
        super(HttpStatus.CONFLICT, "DUPLICATE_RESOURCE", message);
    }
}
