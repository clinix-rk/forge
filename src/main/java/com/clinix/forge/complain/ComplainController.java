package com.clinix.forge.complain;

import com.clinix.forge.complain.dto.ComplainResponse;
import com.clinix.forge.complain.dto.CreateComplainRequest;
import com.clinix.forge.complain.dto.UpdateComplainRequest;
import com.clinix.forge.core.payload.ApiResponse;
import com.clinix.forge.core.payload.PaginatedPayload;
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
@RequestMapping("/patients/{patientId}/complains")
@RequiredArgsConstructor
@Tag(name = "Complain Management", description = "Endpoints for managing patient complaints and categories")
public class ComplainController {

    private final ComplainService complainService;

    @PostMapping
    @Operation(summary = "Register a new complain", description = "Creates a new patient complain record.")
    public ResponseEntity<ApiResponse<ComplainResponse>> createComplain(
            @PathVariable Long patientId,
            @RequestBody @Valid CreateComplainRequest request
    ) {
        log.debug("API call: Create new complain");
        ComplainResponse response = complainService.createComplain(patientId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Complain registered successfully.", response));
    }

    @GetMapping
    @Operation(summary = "Get complains (Paginated)", description = "Retrieves a paginated list of all complains.")
    public ResponseEntity<ApiResponse<PaginatedPayload<ComplainResponse>>> getAllComplains(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number must be greater than or equal to 0.") int pageNo,
            @RequestParam(defaultValue = "10") @Min(value = 5, message = "Page size must be at least 5.") @Max(value = 1000, message = "Page size must be less than or equal to 1000.") int pageSize,
            @PathVariable Long patientId
    ) {
        log.debug("API call: Fetching complains paginated - Page: {}, Size: {}, PatientId: {}", pageNo, pageSize, patientId);
        PaginatedPayload<ComplainResponse> response = complainService.getAllComplains(patientId, pageNo, pageSize);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Complains retrieved successfully.", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get complain by ID", description = "Retrieves a complain's details by ID.")
    public ResponseEntity<ApiResponse<ComplainResponse>> getComplainById(
            @PathVariable Long id,
            @PathVariable Long patientId
    ) {
        log.debug("API call: Fetching complain with ID: {}", id);
        ComplainResponse response = complainService.getComplainById(patientId, id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Complain retrieved successfully.", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update complain by ID", description = "Updates an existing complain record.")
    public ResponseEntity<ApiResponse<ComplainResponse>> updateComplainById(
            @PathVariable Long patientId,
            @PathVariable Long id,
            @RequestBody @Valid UpdateComplainRequest request
    ) {
        log.debug("API call: Updating complain with ID: {}", id);
        ComplainResponse response = complainService.updateComplainById(patientId, id, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Complain updated successfully.", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete complain by ID", description = "Deletes a complain record based on ID.")
    public ResponseEntity<Void> deleteComplainById(
            @PathVariable Long id,
            @PathVariable Long patientId
    ) {
        log.debug("API call: Deleting complain with ID: {}", id);
        complainService.deleteComplainById(patientId, id);
        return ResponseEntity.noContent().build();
    }
}
