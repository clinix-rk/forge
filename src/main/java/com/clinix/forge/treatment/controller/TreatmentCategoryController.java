package com.clinix.forge.treatment.controller;

import com.clinix.forge.core.payload.ApiResponse;
import com.clinix.forge.treatment.dto.CreateTreatmentCategoryRequest;
import com.clinix.forge.treatment.dto.TreatmentCategoryResponse;
import com.clinix.forge.treatment.dto.UpdateTreatmentCategoryRequest;
import com.clinix.forge.treatment.service.TreatmentCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/categories/treatments")
@RequiredArgsConstructor
@Tag(
        name = "Treatment Category Management",
        description = "Endpoints for managing treatment categories"
)
public class TreatmentCategoryController {

    private final TreatmentCategoryService treatmentCategoryService;

    @PostMapping
    @Operation(
            summary = "Create a treatment",
            description = "Creates a new patient treatment record."
    )
    public ResponseEntity<ApiResponse<TreatmentCategoryResponse>> createTreatmentCategory(
            @Valid
            @NotNull(message = "Request body cannot be null.")
            @RequestBody
        CreateTreatmentCategoryRequest request
    ) {
        log.debug("Serving create request : {}", request);

        TreatmentCategoryResponse response = treatmentCategoryService.createTreatmentCategory(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Treatment category created.", response));
    }

    @GetMapping
    @Operation(
            summary = "Get all treatment categories",
            description = "Retrieves a list of all treatment categories."
    )
    public ResponseEntity<ApiResponse<List<TreatmentCategoryResponse>>> getAllTreatmentCategories (
            @RequestParam(value = "null", required = false)
            Long parentId
    ) {
        log.debug("Serving read request : { parentId : {} }", parentId);

        List<TreatmentCategoryResponse> categories = treatmentCategoryService.getAllTreatmentCategories(parentId);

        return ResponseEntity
                .ok(ApiResponse.success("Treatment categories retrieved.", categories));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a treatment",
            description = "Updates an existing patient treatment record."
    )
    public ResponseEntity<ApiResponse<TreatmentCategoryResponse>> updateTreatmentCategoryById (
            @PathVariable(required = true)
            Long id,

            @NotNull
            @Valid
            @RequestBody
            UpdateTreatmentCategoryRequest request
    ) {
        log.debug("Serving update request : { id : {}, request : {} }", id, request);

        TreatmentCategoryResponse response = treatmentCategoryService.updateTreatmentCategoryById(id, request);

        return ResponseEntity.ok(ApiResponse.success("Treatment category updated.", response));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a treatment",
            description = "Deletes an existing patient treatment record."
    )
    public ResponseEntity<ApiResponse<Boolean>> deleteTreatmentCategory (
            @PathVariable(required = true)
            Long id
    ) {
        log.debug("Serving delete request : { id : {} }", id);

        boolean deletionStatus = treatmentCategoryService.deleteTreatmentCategoryById(id);

        return ResponseEntity.ok(ApiResponse.success("Treatment category deleted.", deletionStatus));
    }
}
