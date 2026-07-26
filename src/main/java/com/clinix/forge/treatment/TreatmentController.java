package com.clinix.forge.treatment;

import com.clinix.forge.core.payload.ApiResponse;
import com.clinix.forge.core.payload.PaginatedPayload;
import com.clinix.forge.treatment.dto.*;
import io.swagger.v3.oas.annotations.Operation;
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

@Slf4j
@Validated
@RestController
@RequestMapping("/treatments")
@RequiredArgsConstructor
@Tag(name = "Treatment Management", description = "Endpoints for managing patient treatments and categories")
public class TreatmentController {

    private final TreatmentService treatmentService;

    @PostMapping
    @Operation(summary = "Create a treatment", description = "Creates a new patient treatment record.")
    public ResponseEntity<ApiResponse<TreatmentResponse>> createTreatment(
            @RequestBody @Valid CreateTreatmentRequest request
    ) {
        log.debug("API call: Create new treatment");
        TreatmentResponse response = treatmentService.createTreatment(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Treatment created successfully.", response));
    }

    @GetMapping
    @Operation(summary = "Get treatments (Paginated)", description = "Retrieves a paginated list of all treatments.")
    public ResponseEntity<ApiResponse<PaginatedPayload<TreatmentResponse>>> getAllTreatments(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number must be greater than or equal to 0.") int pageNo,
            @RequestParam(defaultValue = "10") @Min(value = 5, message = "Page size must be at least 5.") @Max(value = 1000, message = "Page size must be less than or equal to 1000.") int pageSize,
            @RequestParam(required = false) Long patientId
    ) {
        log.debug("API call: Fetching treatments paginated - Page: {}, Size: {}, PatientId: {}", pageNo, pageSize, patientId);
        PaginatedPayload<TreatmentResponse> response = treatmentService.getAllTreatments(patientId, pageNo, pageSize);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Treatments retrieved successfully.", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get treatment by ID", description = "Retrieves a treatment's details by ID.")
    public ResponseEntity<ApiResponse<TreatmentResponse>> getTreatmentById(@PathVariable Long id) {
        log.debug("API call: Fetching treatment with ID: {}", id);
        TreatmentResponse response = treatmentService.getTreatmentById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Treatment retrieved successfully.", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update treatment by ID", description = "Updates an existing treatment record.")
    public ResponseEntity<ApiResponse<TreatmentResponse>> updateTreatmentById(
            @PathVariable Long id,
            @RequestBody @Valid UpdateTreatmentRequest request
    ) {
        log.debug("API call: Updating treatment with ID: {}", id);
        TreatmentResponse response = treatmentService.updateTreatmentById(id, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Treatment updated successfully.", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete treatment by ID", description = "Deletes a treatment record based on ID.")
    public ResponseEntity<Void> deleteTreatmentById(@PathVariable Long id) {
        log.debug("API call: Deleting treatment with ID: {}", id);
        treatmentService.deleteTreatmentById(id);
        return ResponseEntity.noContent().build();
    }
}
