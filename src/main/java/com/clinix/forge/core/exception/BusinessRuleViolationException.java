package com.clinix.forge.core.exception;

import org.springframework.http.HttpStatus;

public class BusinessRuleViolationException extends BaseException {

    public BusinessRuleViolationException(String message) {
        super(HttpStatus.UNPROCESSABLE_CONTENT, message);
    }
}
