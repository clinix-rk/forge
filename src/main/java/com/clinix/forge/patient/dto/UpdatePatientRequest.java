package com.clinix.forge.patient.dto;

import com.clinix.forge.patient.entity.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record UpdatePatientRequest(
        @NotBlank(message = "Patient name cannot be blank")
        @Size(max = 50, message = "Name must not exceed 50 characters")
        String name,

        @NotNull(message = "Date of birth is required")
        @Past(message = "Date of birth must be a past date")
        LocalDate dateOfBirth,

        @NotNull(message = "Gender specification is required")
        Gender gender,

        @Email(message = "Invalid email format")
        @Size(max = 100, message = "Email must not exceed 100 characters")
        String email,

        String address,

        @Size(max = 50, message = "City name must not exceed 50 characters")
        String city,

        @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "Pincode must be in Indian pincode format")
        String pincode,

        @Size(max = 50, message = "Referred by text must not exceed 50 characters")
        String referredBy,

        @NotNull(message = "Phone numbers list cannot be null")
        @Size(min = 1, max = 2, message = "A patient must have between 1 and 2 phone numbers")
        @Valid
        List<PhoneNumberRequest> phoneNumbers,

        @Valid
        Set<String> medicalConditions,

        @Valid
        Set<String> drugAllergies
) {}
