package com.clinix.forge.patient.dto;

import com.clinix.forge.patient.entity.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Data transfer object representing the payload to update a new patient entry.
 *
 * <p>Carries user-submitted details required for onboarding a patient into the system.
 * Instances of this record are expected to pass validation prior to processing.
 *
 * @param name              the name of the patient; required field,
 *                          must be at least 3 - 50 characters long
 * @param dateOfBirth       date of birth of the patient; required field,
 *                          must be lower than current date; used for age calculations
 * @param gender            gender of the patient; required field, must have value of {@link Gender}
 * @param email             the email address provided by the patient;
 *                          must follow a proper email format if provided
 * @param address           the residential address of the patient
 * @param city              the city, patient is living in
 * @param pincode           postal code for their address; must be a 6-digit number if provided
 * @param referredBy        person referenced the patient to the clinic
 * @param phoneNumbers      phone numbers of the patient
 * @param medicalConditions medical conditions of the patient
 * @param drugAllergies     any drug allergies to keep in mind for patient
 */
@Schema(description = "Data transfer object for updating a patient.")
public record UpdatePatientRequest(
        @NotBlank(message = "Patient name cannot be blank")
        @Size(min = 3, max = 50, message = "Name must not exceed 50 characters")
        @Schema(description = "Name of the patient", example = "John Doe")
        String name,

        @Past(message = "Date of birth must be a past date")
        @Schema(description = "Date of the birth of the patient")
        LocalDate dateOfBirth,

        @Schema(description = "Gender of the patient", example = "MALE")
        Gender gender,

        @Email(message = "Invalid email format")
        @Size(max = 100, message = "Email must not exceed 100 characters")
        @Schema(description = "Email address provided by the patient",
                example = "someone@example.com")
        String email,

        @Schema(description = "Residential address of the patient")
        String address,

        @Size(max = 50, message = "City name must not exceed 50 characters")
        @Schema(description = "City name, where the patient is residing")
        String city,

        @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "Pincode must be in Indian pincode format")
        @Schema(description = "Pincode of the user's residence.")
        String pincode,

        @Size(max = 50, message = "Referred by text must not exceed 50 characters")
        @Schema(description = "Person who referred the patient to the clinic")
        String referredBy,

        @NotNull(message = "Phone numbers list cannot be null")
        @Size(min = 1, max = 2, message = "A patient must have between 1 and 2 phone numbers")
        @Valid
        @Schema(description = "List of phone numbers provided by the patient")
        List<PhoneNumberRequest> phoneNumbers,

        @Valid
        @Schema(description = "Medical conditions of the patient")
        Set<String> medicalConditions,

        @Valid
        @Schema(description = "Drug allergies of the patient")
        Set<String> drugAllergies
) {
}
