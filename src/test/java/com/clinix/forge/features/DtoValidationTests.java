package com.clinix.forge.features;

import com.clinix.forge.appointment.dto.CreateAppointmentRequest;
import com.clinix.forge.finance.dto.CreatePaymentRequest;
import com.clinix.forge.finance.entity.PaymentMethod;
import com.clinix.forge.user.dto.CreateUserRequest;
import com.clinix.forge.user.entity.Role;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class DtoValidationTests {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testCreateAppointmentRequest_Validation() {
        // Valid request
        CreateAppointmentRequest validRequest = new CreateAppointmentRequest(1L, "Notes", LocalDateTime.now().plusDays(1));
        assertThat(validator.validate(validRequest)).isEmpty();

        // Null patient ID
        CreateAppointmentRequest invalidRequest1 = new CreateAppointmentRequest(null, "Notes", LocalDateTime.now().plusDays(1));
        Set<ConstraintViolation<CreateAppointmentRequest>> violations1 = validator.validate(invalidRequest1);
        assertThat(violations1).isNotEmpty();
        assertThat(violations1.stream().anyMatch(v -> v.getMessage().contains("Patient ID is required"))).isTrue();

        // Negative patient ID
        CreateAppointmentRequest invalidRequest2 = new CreateAppointmentRequest(-5L, "Notes", LocalDateTime.now().plusDays(1));
        Set<ConstraintViolation<CreateAppointmentRequest>> violations2 = validator.validate(invalidRequest2);
        assertThat(violations2).isNotEmpty();
        assertThat(violations2.stream().anyMatch(v -> v.getMessage().contains("Patient ID must be a positive number"))).isTrue();

        // Null datetime
        CreateAppointmentRequest invalidRequest3 = new CreateAppointmentRequest(1L, "Notes", null);
        Set<ConstraintViolation<CreateAppointmentRequest>> violations3 = validator.validate(invalidRequest3);
        assertThat(violations3).isNotEmpty();
        assertThat(violations3.stream().anyMatch(v -> v.getMessage().contains("Appointment date and time is required"))).isTrue();
    }

    @Test
    public void testCreatePaymentRequest_Validation() {
        // Valid request
        CreatePaymentRequest validRequest = new CreatePaymentRequest(1L, 2L, 500.0, PaymentMethod.ONLINE, "REF123");
        assertThat(validator.validate(validRequest)).isEmpty();

        // Negative amount
        CreatePaymentRequest invalidRequest = new CreatePaymentRequest(1L, 2L, -50.0, PaymentMethod.ONLINE, "REF123");
        Set<ConstraintViolation<CreatePaymentRequest>> violations = validator.validate(invalidRequest);
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(v -> v.getMessage().contains("Amount must be a positive number"))).isTrue();
    }

    @Test
    public void testCreateUserRequest_Validation() {
        // Valid request
        CreateUserRequest validRequest = new CreateUserRequest("admin", "securepass", Role.ADMIN);
        assertThat(validator.validate(validRequest)).isEmpty();

        // Short password
        CreateUserRequest invalidRequest = new CreateUserRequest("admin", "short", Role.ADMIN);
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(invalidRequest);
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(v -> v.getMessage().contains("Password must be between 6 and 100 characters"))).isTrue();
    }
}
