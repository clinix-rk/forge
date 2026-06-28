package com.clinix.forge.doctors;

import com.clinix.forge.core.payload.ApiResponse;
import com.clinix.forge.core.payload.PaginatedPayload;
import com.clinix.forge.doctors.dto.CreateDoctorRequest;
import com.clinix.forge.doctors.dto.DoctorResponse;
import com.clinix.forge.doctors.dto.UpdateDoctorRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing doctor records.
 * Provides endpoints for creating, retrieving, updating, and deleting doctors.
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctor Management", description = "Endpoints for managing doctors in the Clinix system")
public class DoctorController {

    private final DoctorService doctorService;

    /**
     * Registers a new doctor in the system.
     *
     * @param dto the data to create a new doctor
     * @return the created doctor details
     */
    @PostMapping
    @Operation(summary = "Add a new doctor", description = "Creates a new doctor record with the provided details.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Doctor record saved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<ApiResponse<DoctorResponse>> addDoctor(
            @RequestBody @Valid CreateDoctorRequest dto
    ) {
        log.debug("Creating a new doctor record");
        DoctorResponse savedDoctor = doctorService.createDoctor(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Doctor record has been successfully saved to the system.",
                        savedDoctor
                ));
    }

    /**
     * Retrieves a doctor by their unique identifier.
     *
     * @param id the ID of the doctor to retrieve
     * @return the doctor details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get doctor by ID", description = "Retrieves a single doctor's details by their database ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Doctor record retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    public ResponseEntity<ApiResponse<DoctorResponse>> getDoctorById(
            @PathVariable Long id
    ) {
        log.debug("Fetching doctor with ID: {}", id);
        DoctorResponse doctor = doctorService.getDoctorById(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        "Doctor record retrieved successfully.",
                        doctor
                ));
    }

    /**
     * Retrieves a paginated list of doctors.
     *
     * @param pageNo   the page number to retrieve (starts from 0)
     * @param pageSize the number of records per page
     * @return a paginated payload of doctor details
     */
    @GetMapping
    @Operation(summary = "Get all doctors (Paginated)", description = "Retrieves a list of doctors with support for pagination.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Doctor records retrieved successfully")
    })
    public ResponseEntity<ApiResponse<PaginatedPayload<DoctorResponse>>> getDoctors(
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page number must be greater than or equal to 0.")
            int pageNo,

            @RequestParam(defaultValue = "10")
            @Min(value = 5, message = "Page size must be at least 5.")
            @Max(value = 1000, message = "Page size must be less than or equal to 1000")
            int pageSize
    ) {
        log.debug("Fetching doctors list - Page: {}, Size: {}", pageNo, pageSize);
        PaginatedPayload<DoctorResponse> doctors = doctorService.getPaginatedDoctors(PageRequest.of(pageNo, pageSize));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        "Doctor records retrieved successfully.",
                        doctors
                ));
    }

    /**
     * Updates an existing doctor's record by their ID.
     *
     * @param id  the ID of the doctor to update
     * @param dto the updated doctor details
     * @return the updated doctor details
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update doctor by ID", description = "Updates the information of an existing doctor.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Doctor record updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    public ResponseEntity<ApiResponse<DoctorResponse>> updateDoctorById(
            @PathVariable Long id,
            @RequestBody @Valid UpdateDoctorRequest dto
    ) {
        log.debug("Updating doctor with ID: {}", id);
        DoctorResponse updatedDoctor = doctorService.updateDoctorById(id, dto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        "Doctor record updated successfully.",
                        updatedDoctor
                ));
    }

    /**
     * Deletes a doctor's record from the system by their ID.
     * Note: Current implementation uses update method, consider implementing a proper delete in service.
     *
     * @param id the ID of the doctor to delete
     * @return an empty response indicating success
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete doctor by ID", description = "Performs a deletion of a doctor record based on ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Doctor record deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    public ResponseEntity<Void> deleteDoctorById(
            @PathVariable Long id
    ) {
        log.debug("Deleting doctor with ID: {}", id);

        doctorService.deleteDoctorById(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Searches for doctors by their name.
     *
     * @param name the name fragment to search for
     * @return list of matching doctor details
     */
    @GetMapping("/search")
    @Operation(summary = "Search doctors by name", description = "Retrieves a list of doctors matching the name query fragment.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Doctors matched successfully")
    })
    public ResponseEntity<ApiResponse<java.util.List<DoctorResponse>>> searchDoctors(
            @RequestParam String name
    ) {
        log.debug("API call: Searching doctors matching name: {}", name);
        java.util.List<DoctorResponse> doctors = doctorService.searchDoctorsByName(name);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        "Doctor records matched successfully.",
                        doctors
                ));
    }
}