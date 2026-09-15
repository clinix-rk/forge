package com.clinix.forge.patient.dto;

import com.clinix.forge.patient.entity.Gender;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Data transfer object for returning a patient's information from the api
 *
 * @param id                System id given to the patient record
 * @param caseNo            Case number of the patient
 * @param name              name of the patient
 * @param dateOfBirth       date of birth of the patient
 * @param gender            gender of the patient
 * @param email             email of the patient
 * @param address           address of the patient
 * @param city              city of residence of the patient
 * @param pincode           pincode of patient's residence
 * @param referredBy        person to remember for the patient for reference
 * @param phoneNumbers      list of phone numbers of patient; 1 - 2 numbers
 * @param medicalConditions list of medical conditions
 * @param drugAllergies     list of drug allergies
 * @param createdAt         timestamp at which the record was added
 * @param updatedAt         timestamp at which the record was last updated
 */
public record PatientResponse(
        @Schema(
                description = "System id for patient record",
                example = "1"
        )
        Long id,

        @Schema(
                description = "Case number of the patient",
                example = "Y00001"
        )
        String caseNo,

        @Schema(
                description = "Full name of the patient",
                example = "John Doe"
        )
        String name,

        @Schema(
                description = "Patient's date of birth to mainly calculate age",
                example = "1970-01-01"
        )
        LocalDate dateOfBirth,

        @Schema(
                description = "Patients gender (can be MALE, FEMALE or OTHER)",
                example = "MALE"
        )
        Gender gender,

        @Schema(
                description = "Email address of patient",
                example = "someone@example.com"
        )
        String email,

        @Schema(
                description = "Patient home address",
                example = "Delhi"
        )
        String address,

        @Schema(
                description = "Residence city",
                example = "Delhi"
        )
        String city,

        @Schema(
                description = "Postal code for the address",
                example = "123456"
        )
        String pincode,

        @Schema(
                description = "The doctor whose reference was brought by the patient",
                example = "Dr. Jane Doe"
        )
        String referredBy,

        @Schema(description = "List of Primary and Secondary phone numbers")
        List<PhoneNumberResponse> phoneNumbers,

        @Schema(description = "Patient's medical conditions")
        Set<String> medicalConditions,

        @Schema(description = "Patient's drug allergies")
        Set<String> drugAllergies,

        @Schema(description = "The system metadata for when the patient record was created")
        Instant createdAt,

        @Schema(description = "The system metadata for when the patient record was last updated")
        Instant updatedAt
) {
}
