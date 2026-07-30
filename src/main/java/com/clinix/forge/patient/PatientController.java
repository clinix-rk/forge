package com.clinix.forge.patient;

import com.clinix.forge.core.payload.ApiResponse;
import com.clinix.forge.core.payload.PaginatedPayload;
import com.clinix.forge.patient.dto.CreatePatientRequest;
import com.clinix.forge.patient.dto.PatientResponse;
import com.clinix.forge.patient.dto.UpdatePatientRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing patient records.
 * Provides endpoints for creating, retrieving, updating, deleting, and searching patients.
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
@Tag(name = "Patient Management", description = "Endpoints for managing patients in the Clinix system")
public class PatientController {

    private final PatientService patientService;

    /**
     * Registers a new patient in the system.
     * Generates their case number and increments the associated doctor's patient count.
     *
     * @param request the data to create a new patient
     * @return the created patient details
     */
    @PostMapping
    @Operation(summary = "Add a new patient", description = "Creates a new patient record with automatic case number generation.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Patient record saved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    public ResponseEntity<ApiResponse<PatientResponse>> addPatient(
            @RequestBody @Valid CreatePatientRequest request
    ) {
        log.debug("API call: Create a new patient record");
        PatientResponse savedPatient = patientService.createPatient(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Patient record has been successfully saved to the system.",
                        savedPatient
                ));
    }

    /**
     * Retrieves a patient by their unique database ID.
     *
     * @param id the database ID of the patient
     * @return the patient details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get patient by ID", description = "Retrieves a single patient's details by their database ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Patient record retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<ApiResponse<PatientResponse>> getPatientById(
            @PathVariable Long id
    ) {
        log.debug("API call: Fetching patient with ID: {}", id);
        PatientResponse patient = patientService.getPatientById(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        "Patient record retrieved successfully.",
                        patient
                ));
    }

    /**
     * Retrieves a patient by their generated case number.
     *
     * @param caseNo the case number of the patient
     * @return the patient details
     */
    @GetMapping("/case-no/{caseNo}")
    @Operation(summary = "Get patient by Case Number", description = "Retrieves a single patient's details by their case number.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Patient record retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<ApiResponse<PatientResponse>> getPatientByCaseNo(
            @PathVariable String caseNo
    ) {
        log.debug("API call: Fetching patient with Case No: {}", caseNo);
        PatientResponse patient = patientService.getPatientByCaseNo(caseNo);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        "Patient record retrieved successfully.",
                        patient
                ));
    }

    /**
     * Search and retrieve patients with pagination.
     *
     * @param pageNo   1-based page number to retrieve
     * @param pageSize the number of records per page
     * @param name     optional search name
     * @param phoneNo  optional search phone number
     * @param caseNo   optional search case number
     * @return a paginated payload of patient details
     */
    @GetMapping
    @Operation(summary = "Search/Get patients (Paginated)", description = "Retrieves a paginated list of patients filtered by search criteria.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Patient records retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid validation parameters")
    })
    public ResponseEntity<ApiResponse<PaginatedPayload<PatientResponse>>> searchPatients(
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page number must be greater than or equal to 0.")
            int pageNo,

            @RequestParam(defaultValue = "10")
            @Min(value = 5, message = "Page size must be at least 5.")
            @Max(value = 1000, message = "Page size must be less than or equal to 1000.")
            int pageSize,

            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phoneNo,
            @RequestParam(required = false) String caseNo
    ) {
        log.debug("API call: Searching patients - Page: {}, Size: {}, Name: {}, Phone: {}, CaseNo: {}",
                pageNo, pageSize, name, phoneNo, caseNo);

        PaginatedPayload<PatientResponse> patients = patientService.searchPatients(
                name, phoneNo, caseNo, pageNo, pageSize);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        "Patient records retrieved successfully.",
                        patients
                ));
    }

    /**
     * Updates an existing patient record.
     *
     * @param id      the ID of the patient to update
     * @param request the updated patient details
     * @return the updated patient details
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update patient by ID", description = "Updates the information of an existing patient record.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Patient record updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<ApiResponse<PatientResponse>> updatePatientById(
            @PathVariable Long id,
            @RequestBody @Valid UpdatePatientRequest request
    ) {
        log.debug("API call: Updating patient with ID: {}", id);
        PatientResponse updatedPatient = patientService.updatePatientById(id, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        "Patient record updated successfully.",
                        updatedPatient
                ));
    }

    @GetMapping("/medical-conditions")
    @Operation(summary = "Get all medical conditions", description = "Retrieves a list of all distinct medical conditions.")
    public ResponseEntity<ApiResponse<List<String>>> getAllMedicalConditions() {
        log.debug("API call: Fetching all medical conditions");
        List<String> conditions = patientService.getAllMedicalConditions();
        return ResponseEntity.ok(ApiResponse.success("Medical conditions retrieved successfully.", conditions));
    }

    @GetMapping("/drug-allergies")
    @Operation(summary = "Get all drug allergies", description = "Retrieves a list of all distinct drug allergies.")
    public ResponseEntity<ApiResponse<List<String>>> getAllDrugAllergies() {
        log.debug("API call: Fetching all drug allergies");
        List<String> allergies = patientService.getAllDrugAllergies();
        return ResponseEntity.ok(ApiResponse.success("Drug allergies retrieved successfully.", allergies));
    }

    /**
     * Deletes a patient record from the system.
     *
     * @param id the ID of the patient to delete
     * @return 204 No Content response
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete patient by ID", description = "Deletes a patient record based on ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Patient record deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<Void> deletePatientById(
            @PathVariable Long id
    ) {
        log.debug("API call: Deleting patient with ID: {}", id);
        patientService.deletePatientById(id);
        return ResponseEntity.noContent().build();
    }
}

