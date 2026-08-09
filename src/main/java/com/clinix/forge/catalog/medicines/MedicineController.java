package com.clinix.forge.catalog.medicines;


import com.clinix.forge.catalog.medicines.dto.CreateMedicineRequest;
import com.clinix.forge.catalog.medicines.dto.MedicineResponse;
import com.clinix.forge.catalog.medicines.dto.UpdateMedicineRequest;
import com.clinix.forge.core.payload.ApiResponse;
import com.clinix.forge.core.payload.PaginationMetadata;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/catalog/medicines")
@RequiredArgsConstructor
@Tag(name = "Medicine Catalog Management", description = "Endpoints for managing medicine catalog items")
public class MedicineController {

    private final MedicineService medicineService;

    @PostMapping
    @Operation(summary = "Add a medicine", description = "Creates a new medicine catalog item.")
    public ResponseEntity<ApiResponse<MedicineResponse>> createMedicine(
            @RequestBody @Valid CreateMedicineRequest request
    ) {
        log.debug("API call: Create new medicine catalog entry");
        MedicineResponse response = medicineService.createMedicine(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Get medicines (Paginated)", description = "Retrieves a paginated list of all medicine items.")
    public ResponseEntity<ApiResponse<java.util.List<MedicineResponse>>> getAllMedicines(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number must be greater than or equal to 0.") int pageNo,
            @RequestParam(defaultValue = "10") @Min(value = 5, message = "Page size must be at least 5.") @Max(value = 1000, message = "Page size must be less than or equal to 1000.") int pageSize
    ) {
        log.debug("API call: Fetching medicines paginated - Page: {}, Size: {}", pageNo, pageSize);
        Page<MedicineResponse> response = medicineService.getAllMedicines(pageNo, pageSize);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response.getContent(), new PaginationMetadata(response.getNumber(), response.getSize(), response.getTotalElements(), response.getTotalPages(), response.hasNext(), response.hasPrevious())));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get medicine by ID", description = "Retrieves a medicine's details by ID.")
    public ResponseEntity<ApiResponse<MedicineResponse>> getMedicineById(@PathVariable Long id) {
        log.debug("API call: Fetching medicine with ID: {}", id);
        MedicineResponse response = medicineService.getMedicineById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update medicine by ID", description = "Updates an existing medicine details.")
    public ResponseEntity<ApiResponse<MedicineResponse>> updateMedicineById(
            @PathVariable Long id,
            @RequestBody @Valid UpdateMedicineRequest request
    ) {
        log.debug("API call: Updating medicine with ID: {}", id);
        MedicineResponse response = medicineService.updateMedicineById(id, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete medicine by ID", description = "Deletes a medicine catalog entry based on ID.")
    public ResponseEntity<Void> deleteMedicineById(@PathVariable Long id) {
        log.debug("API call: Deleting medicine with ID: {}", id);
        medicineService.deleteMedicineById(id);
        return ResponseEntity.noContent().build();
    }
}
