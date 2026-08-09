package com.clinix.forge.core.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseException {
    public ResourceNotFoundException(Long userId) {
        super(
                HttpStatus.NOT_FOUND,
                "USER_NOT_FOUND",
                "User with id " + userId + " not found"
        );
    }

    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", message);
    }
}
