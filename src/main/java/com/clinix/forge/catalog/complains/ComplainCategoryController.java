package com.clinix.forge.catalog.complains;

import com.clinix.forge.catalog.complains.dto.ComplainCategoryResponse;
import com.clinix.forge.catalog.complains.dto.CreateComplainCategoryRequest;
import com.clinix.forge.catalog.complains.dto.UpdateComplainCategoryRequest;
import com.clinix.forge.core.payload.ApiResponse;
import com.clinix.forge.core.payload.PaginationMetadata;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
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
        description = "Endpoints to manage complain categories"
)
public class ComplainCategoryController {

    private final ComplainCategoryService complainCategoryService;

    @PostMapping
    @Operation(
            summary = "Create a new complain category",
            description = "Adds a new complain category to the system."
    )
    public ResponseEntity<ApiResponse<ComplainCategoryResponse>> createComplainCategory(
            @RequestBody
            @Valid
            CreateComplainCategoryRequest request
    ) {
        ComplainCategoryResponse response = complainCategoryService.createComplainCategory(request);

        String traceId = MDC.get("traceId");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(
            summary = "Get all complain categories",
            description = "Retrieves all the complain categories."
    )
    public ResponseEntity<ApiResponse<java.util.List<ComplainCategoryResponse>>> getAllComplainCategories(
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page number must be greater than or equal to 0.")
            int pageNo,

            @RequestParam(defaultValue = "10")
            @Min(value = 5, message = "Page size must be at least 5.")
            @Max(value = 1000, message = "Page size must be less than or equal to 1000.")
            int pageSize
    ) {
        Page<ComplainCategoryResponse> response = complainCategoryService.getPaginatedComplainCategories(pageNo, pageSize);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(response.getContent(), new PaginationMetadata(response.getNumber(), response.getSize(), response.getTotalElements(), response.getTotalPages(), response.hasNext(), response.hasPrevious())));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get all sub-categories",
            description = "Sends back a list of complain categories under the requested category."
    )
    public ResponseEntity<ApiResponse<java.util.List<ComplainCategoryResponse>>> getallSubCategoriesForId(
            @RequestParam(defaultValue = "1")
            @Min(value = 1, message = "Page number must be greater than or equal to 1.")
            int pageNo,

            @RequestParam(defaultValue = "10")
            @Min(value = 5, message = "Page size must be at least 5.")
            @Max(value = 1000, message = "Page size must be less than or equal to 1000.")
            int pageSize
    ) {
        Page<ComplainCategoryResponse> response = complainCategoryService.getPaginatedComplainCategories(pageNo, pageSize);

        return ResponseEntity.ok(ApiResponse.success(response.getContent(), new PaginationMetadata(response.getNumber(), response.getSize(), response.getTotalElements(), response.getTotalPages(), response.hasNext(), response.hasPrevious())));
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
        ComplainCategoryResponse response = complainCategoryService.updateComplainCategoryById(id, request);

        return ResponseEntity.ok(ApiResponse.success(response));
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
        boolean successStatus = complainCategoryService.deleteComplainCategoryById(id);

        return ResponseEntity.ok(ApiResponse.success(successStatus));
    }
}
