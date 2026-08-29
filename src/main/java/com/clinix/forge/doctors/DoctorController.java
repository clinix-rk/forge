package com.clinix.forge.doctors;

import com.clinix.forge.core.payload.ClinixApiResponse;
import com.clinix.forge.core.payload.PaginationMetadata;
import com.clinix.forge.doctors.dto.CreateDoctorRequest;
import com.clinix.forge.doctors.dto.DoctorResponse;
import com.clinix.forge.doctors.dto.UpdateDoctorRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing doctor records.
 * Provides endpoints for creating, retrieving, updating, and deleting doctors.
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctor management", description = "Endpoints to manage doctor records")
public class DoctorController {

    private final DoctorService doctorService;

    /**
     * Registers a new doctor record.
     *
     * @param request the data to create a new doctor
     * @return the created doctor details
     */
    @PostMapping
    @Operation(
            summary = "Add doctor",
            description = "Creates a new doctor record with the provided details."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Successfully added the doctor record"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid data for doctor creation"
            )
    })
    public ResponseEntity<ClinixApiResponse<DoctorResponse>> addDoctor(
            @RequestBody
            @Valid
            CreateDoctorRequest request
    ) {
        log.debug("Received get request to add doctor with data: {}", request);

        DoctorResponse doctorResponse = doctorService.createDoctor(request);

        log.info("Added a new doctor.");
        log.debug("Doctor id: {}", doctorResponse.id());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ClinixApiResponse.success(doctorResponse));
    }

    /**
     * Retrieves a doctor by their unique identifier.
     *
     * @param id the ID of the doctor to retrieve
     * @return the doctor details
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Get doctor by ID",
            description = "Retrieves a single doctor's details by their database ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Doctor record retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor not found"
            )
    })
    public ResponseEntity<ClinixApiResponse<DoctorResponse>> getDoctorById(
            @PathVariable Long id
    ) {
        log.debug("Received GET request for fetching doctor information for doctor id {}", id);

        DoctorResponse doctor = doctorService.getDoctorById(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ClinixApiResponse.success(doctor));
    }

    /**
     * Retrieves a paginated list of doctors.
     *
     * @param pageNo   the page number to retrieve (starts from 0)
     * @param pageSize the number of records per page
     * @return a paginated payload of doctor details
     */
    @GetMapping
    @Operation(
            summary = "Get all doctors (Paginated)",
            description = "Retrieves a list of doctors with support for pagination."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Doctor records retrieved successfully"
            )
    })
    public ResponseEntity<ClinixApiResponse<List<DoctorResponse>>> getDoctors(
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page number must be greater than or equal to 0.")
            int pageNo,

            @RequestParam(defaultValue = "10")
            @Min(value = 5, message = "Page size must be at least 5.")
            @Max(value = 1000, message = "Page size must be less than or equal to 1000")
            int pageSize
    ) {
        log.debug("Received GET request to fetch doctors in paginated format. " +
                "Pagination data, pageNo: {}, pageSize: {}", pageNo, pageSize);

        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);

        Page<DoctorResponse> doctors = doctorService.getPaginatedDoctors(pageRequest);

        PaginationMetadata metadata = new PaginationMetadata(
                doctors.getNumber(),
                doctors.getSize(),
                doctors.getTotalElements(),
                doctors.getTotalPages(),
                doctors.hasNext(),
                doctors.hasPrevious()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ClinixApiResponse.success(doctors.getContent(), metadata));
    }

    /**
     * Updates an existing doctor's record by their ID.
     *
     * @param id  the ID of the doctor to update
     * @param dto the updated doctor details
     * @return the updated doctor details
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Update doctor by ID",
            description = "Updates the information of an existing doctor."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Doctor record updated successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor not found"
            )
    })
    public ResponseEntity<ClinixApiResponse<DoctorResponse>> updateDoctorById(
            @PathVariable Long id,
            @RequestBody @Valid UpdateDoctorRequest dto
    ) {
        log.debug("Received PUT request to update doctor with id {}", id);

        DoctorResponse updatedDoctor = doctorService.updateDoctorById(id, dto);

        log.info("Updated doctor with id {}", updatedDoctor.id());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ClinixApiResponse.success(updatedDoctor));
    }

    /**
     * Deletes a doctor's record from the system by their ID.
     *
     * @param id the ID of the doctor to delete
     * @return an empty response indicating success
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete doctor by ID",
            description = "Performs a deletion of a doctor record based on ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Doctor record deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor not found"
            )
    })
    public ResponseEntity<Void> deleteDoctorById(
            @PathVariable Long id
    ) {
        log.debug("Received DELETE request to delete doctor with id {}", id);

        doctorService.deleteDoctorById(id);

        log.info("Deleted doctor with id {}", id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Searches for doctors by their name.
     *
     * @param name the name fragment to search for
     * @return list of matching doctor details
     */
    @GetMapping("/search")
    @Operation(
            summary = "Search doctors by name",
            description = "Retrieves a list of doctors matching the name query fragment."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Doctors matched successfully"
            )
    })
    public ResponseEntity<ClinixApiResponse<List<DoctorResponse>>> searchDoctors(
            @RequestParam String name
    ) {
        log.debug("API call: Searching doctors matching name: {}", name);
        java.util.List<DoctorResponse> doctors = doctorService.searchDoctorsByName(name);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ClinixApiResponse.success(doctors));
    }
}
