package com.clinix.forge.catalog.prescription.instructions;

import com.clinix.forge.catalog.prescription.instructions.dto.CreateInstructionRequest;
import com.clinix.forge.catalog.prescription.instructions.dto.InstructionResponse;
import com.clinix.forge.catalog.prescription.instructions.dto.UpdateInstructionRequest;
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
@RequestMapping("/catalog/instructions")
@RequiredArgsConstructor
@Tag(name = "Instruction catalog management", description = "Endpoints for managing drug instructions")
public class InstructionController {

    private final InstructionService instructionService;

    @PostMapping
    @Operation(summary = "Add a drug instruction pattern", description = "Creates a new drug dosage pattern.")
    public ResponseEntity<ApiResponse<InstructionResponse>> createDrugInstruction(
            @RequestBody @Valid CreateInstructionRequest request
    ) {
        log.debug("API call: Create new drug instruction pattern");
        InstructionResponse response = instructionService.createDrugInstruction(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Get drug instructions (Paginated)", description = "Retrieves a paginated list of all drug dosage patterns.")
    public ResponseEntity<ApiResponse<java.util.List<InstructionResponse>>> getAllDrugInstructions(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number must be greater than or equal to 0.") int pageNo,
            @RequestParam(defaultValue = "10") @Min(value = 5, message = "Page size must be at least 5.") @Max(value = 1000, message = "Page size must be less than or equal to 1000.") int pageSize
    ) {
        log.debug("API call: Fetching instructions paginated - Page: {}, Size: {}", pageNo, pageSize);
        Page<InstructionResponse> response = instructionService.getAllDrugInstructions(pageNo, pageSize);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response.getContent(), new PaginationMetadata(response.getNumber(), response.getSize(), response.getTotalElements(), response.getTotalPages(), response.hasNext(), response.hasPrevious())));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get drug instruction by ID", description = "Retrieves a drug dosage pattern's details by ID.")
    public ResponseEntity<ApiResponse<InstructionResponse>> getDrugInstructionById(@PathVariable Long id) {
        log.debug("API call: Fetching instruction with ID: {}", id);
        InstructionResponse response = instructionService.getDrugInstructionById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update drug instruction by ID", description = "Updates an existing drug dosage pattern details.")
    public ResponseEntity<ApiResponse<InstructionResponse>> updateDrugInstructionById(
            @PathVariable Long id,
            @RequestBody @Valid UpdateInstructionRequest request
    ) {
        log.debug("API call: Updating instruction with ID: {}", id);
        InstructionResponse response = instructionService.updateDrugInstructionById(id, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete drug instruction by ID", description = "Deletes a drug dosage pattern based on ID.")
    public ResponseEntity<Void> deleteDrugInstructionById(@PathVariable Long id) {
        log.debug("API call: Deleting instruction with ID: {}", id);
        instructionService.deleteDrugInstructionById(id);
        return ResponseEntity.noContent().build();
    }
}
