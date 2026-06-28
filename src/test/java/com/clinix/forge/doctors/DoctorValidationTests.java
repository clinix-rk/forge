package com.clinix.forge.doctors;

import com.clinix.forge.doctors.dto.CreateDoctorRequest;
import com.clinix.forge.doctors.dto.UpdateDoctorRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class DoctorValidationTests {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testCreateDoctorRequest_Valid() {
        CreateDoctorRequest request = new CreateDoctorRequest("Dr. Rut Koticha", "RK");
        Set<ConstraintViolation<CreateDoctorRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    public void testCreateDoctorRequest_BlankName() {
        CreateDoctorRequest request = new CreateDoctorRequest("", "RK");
        Set<ConstraintViolation<CreateDoctorRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(v -> v.getMessage().contains("Doctor name is required"))).isTrue();
    }

    @Test
    public void testCreateDoctorRequest_ShortName() {
        CreateDoctorRequest request = new CreateDoctorRequest("A", "RK");
        Set<ConstraintViolation<CreateDoctorRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(v -> v.getMessage().contains("Doctor name must be between 2 and 100 characters"))).isTrue();
    }

    @Test
    public void testCreateDoctorRequest_BlankPrefix() {
        CreateDoctorRequest request = new CreateDoctorRequest("Dr. Rut Koticha", "");
        Set<ConstraintViolation<CreateDoctorRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(v -> v.getMessage().contains("Case number prefix is required"))).isTrue();
    }

    @Test
    public void testCreateDoctorRequest_LongPrefix() {
        CreateDoctorRequest request = new CreateDoctorRequest("Dr. Rut Koticha", "ABCDEF");
        Set<ConstraintViolation<CreateDoctorRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(v -> v.getMessage().contains("Case number prefix cannot exceed 5 characters"))).isTrue();
    }

    @Test
    public void testUpdateDoctorRequest_Valid() {
        UpdateDoctorRequest request = new UpdateDoctorRequest("Dr. Rut Koticha");
        Set<ConstraintViolation<UpdateDoctorRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    public void testUpdateDoctorRequest_Invalid() {
        UpdateDoctorRequest request = new UpdateDoctorRequest("");
        Set<ConstraintViolation<UpdateDoctorRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
    }
}
