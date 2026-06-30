package com.clinix.forge.complain.controller;

import com.clinix.forge.complain.dto.ComplainCategoryResponse;
import com.clinix.forge.complain.dto.CreateComplainCategoryRequest;
import com.clinix.forge.complain.dto.UpdateComplainCategoryRequest;
import com.clinix.forge.complain.service.ComplainCategoryService;
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
@RequestMapping("/categories/complains")
@RequiredArgsConstructor
@Tag(
        name = "Complain Category Management",
        description = "Endpoints for managing complain categories"
)
public class ComplainCategoryController {

    private final ComplainCategoryService complainCategoryService;

    @PostMapping
    @Operation(
            summary = "Register a new complain category",
            description = "Creates a new complain category record."
    )
    public ResponseEntity<ApiResponse<ComplainCategoryResponse>> createComplainCategory(
            @RequestBody
            @Valid
            CreateComplainCategoryRequest request
    ) {
        log.debug("Serving create request : { name : {} }", request.name());

        ComplainCategoryResponse response = complainCategoryService.createComplainCategory(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("New complain category created.", response));
    }

    @GetMapping
    @Operation(
            summary = "Get complain categories (Paginated)",
            description = "Retrieves a paginated list of all complain categories."
    )
    public ResponseEntity<ApiResponse<PaginatedPayload<ComplainCategoryResponse>>> getAllComplainCategories(
            @RequestParam(defaultValue = "1")
            @Min(value = 1, message = "Page number must be greater than or equal to 1.")
            int pageNo,

            @RequestParam(defaultValue = "10")
            @Min(value = 5, message = "Page size must be at least 5.")
            @Max(value = 1000, message = "Page size must be less than or equal to 1000.")
            int pageSize
    ) {
        log.debug("Serving read request : { pageNo: {}, pageSize: {} }", pageNo, pageSize);

        PaginatedPayload<ComplainCategoryResponse> response = complainCategoryService.getPaginatedComplainCategories(pageNo, pageSize);

        return ResponseEntity.ok(ApiResponse.success("Fetched complain categories", response));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update Complain Category",
            description = "Updates an existing complain category."
    )
    public ResponseEntity<ApiResponse<ComplainCategoryResponse>> updateComplainCategory(
            @PathVariable(name = "id", required = true)
            Long id,

            @RequestBody
            @Valid
            UpdateComplainCategoryRequest request
    ) {
        log.debug("Serving update request : { id: {} }", id);

        ComplainCategoryResponse response = complainCategoryService.updateComplainCategoryById(id, request);

        return ResponseEntity.ok(ApiResponse.success("Updated complain category", response));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a complain category",
            description = "Deletes a complain category record from database"
    )
    public ResponseEntity<ApiResponse<Boolean>> getAllComplainCategories(
            @PathVariable(required = true)
            Long id
    ) {
        log.debug("Serving delete request : { id: {} }", id);

        boolean successStatus = complainCategoryService.deleteComplainCategoryById(id);

        return ResponseEntity.ok(ApiResponse.success("Deleted complain category", successStatus));
    }
}