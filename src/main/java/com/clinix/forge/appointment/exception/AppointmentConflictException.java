package com.clinix.forge.appointment.exception;

import com.clinix.forge.core.exception.DuplicateResourceException;

public class AppointmentConflictException extends DuplicateResourceException {

    public AppointmentConflictException(String message) {
        super(message);
    }
}
