package com.clinix.forge.catalog.dosages;

import com.clinix.forge.catalog.dosages.dto.CreateDrugDosageRequest;
import com.clinix.forge.catalog.dosages.dto.DrugDosageResponse;
import com.clinix.forge.catalog.dosages.dto.UpdateDrugDosageRequest;
import com.clinix.forge.core.payload.ApiResponse;
import com.clinix.forge.core.payload.PaginatedPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/catalog/dosages")
@RequiredArgsConstructor
@Tag(name = "Dosage catalog management", description = "Endpoints for managing drug dosages")
public class DosageController {

    private final DosageService dosageService;

    @PostMapping("/dosages")
    @Operation(summary = "Add a drug dosage pattern", description = "Creates a new drug dosage pattern.")
    public ResponseEntity<ApiResponse<DrugDosageResponse>> createDrugDosage(
            @RequestBody @Valid CreateDrugDosageRequest request
    ) {
        log.debug("API call: Create new drug dosage pattern");
        DrugDosageResponse response = dosageService.createDrugDosage(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Drug dosage pattern created successfully.", response));
    }

    @GetMapping("/dosages")
    @Operation(summary = "Get drug dosages (Paginated)", description = "Retrieves a paginated list of all drug dosage patterns.")
    public ResponseEntity<ApiResponse<PaginatedPayload<DrugDosageResponse>>> getAllDrugDosages(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number must be greater than or equal to 0.") int pageNo,
            @RequestParam(defaultValue = "10") @Min(value = 5, message = "Page size must be at least 5.") @Max(value = 1000, message = "Page size must be less than or equal to 1000.") int pageSize
    ) {
        log.debug("API call: Fetching dosages paginated - Page: {}, Size: {}", pageNo, pageSize);
        PaginatedPayload<DrugDosageResponse> response = dosageService.getAllDrugDosages(pageNo, pageSize);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Drug dosages retrieved successfully.", response));
    }

    @GetMapping("/dosages/{id}")
    @Operation(summary = "Get drug dosage by ID", description = "Retrieves a drug dosage pattern's details by ID.")
    public ResponseEntity<ApiResponse<DrugDosageResponse>> getDrugDosageById(@PathVariable Long id) {
        log.debug("API call: Fetching dosage with ID: {}", id);
        DrugDosageResponse response = dosageService.getDrugDosageById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Drug dosage retrieved successfully.", response));
    }

    @PutMapping("/dosages/{id}")
    @Operation(summary = "Update drug dosage by ID", description = "Updates an existing drug dosage pattern details.")
    public ResponseEntity<ApiResponse<DrugDosageResponse>> updateDrugDosageById(
            @PathVariable Long id,
            @RequestBody @Valid UpdateDrugDosageRequest request
    ) {
        log.debug("API call: Updating dosage with ID: {}", id);
        DrugDosageResponse response = dosageService.updateDrugDosageById(id, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Drug dosage updated successfully.", response));
    }

    @DeleteMapping("/dosages/{id}")
    @Operation(summary = "Delete drug dosage by ID", description = "Deletes a drug dosage pattern based on ID.")
    public ResponseEntity<Void> deleteDrugDosageById(@PathVariable Long id) {
        log.debug("API call: Deleting dosage with ID: {}", id);
        dosageService.deleteDrugDosageById(id);
        return ResponseEntity.noContent().build();
    }
}
