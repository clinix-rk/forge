package com.clinix.forge.suggestion;

import com.clinix.forge.core.payload.ApiResponse;
import com.clinix.forge.core.payload.PaginatedPayload;
import com.clinix.forge.suggestion.dto.CreateSuggestionRequest;
import com.clinix.forge.suggestion.dto.SuggestionResponse;
import com.clinix.forge.suggestion.dto.UpdateSuggestionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/suggestions")
@RequiredArgsConstructor
@Tag(name = "Suggestion Management", description = "Endpoints for managing suggestions")
public class SuggestionController {

    private final SuggestionService suggestionService;

    @PostMapping
    @Operation(summary = "Create a suggestion", description = "Proposes a new suggestion to a patient.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Suggestion created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<ApiResponse<SuggestionResponse>> createSuggestion(
            @RequestBody @Valid CreateSuggestionRequest request
    ) {
        log.debug("API call: Create a new suggestion record");
        SuggestionResponse response = suggestionService.createSuggestion(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Suggestion created successfully.", response));
    }

    @GetMapping
    @Operation(summary = "Get suggestions (Paginated)", description = "Retrieves a paginated list of suggestions.")
    public ResponseEntity<ApiResponse<PaginatedPayload<SuggestionResponse>>> getAllSuggestions(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number must be greater than or equal to 0.") int pageNo,
            @RequestParam(defaultValue = "10") @Min(value = 5, message = "Page size must be at least 5.") @Max(value = 1000, message = "Page size must be less than or equal to 1000.") int pageSize,
            @RequestParam(required = false) Long patientId
    ) {
        log.debug("API call: Fetching suggestions paginated - Page: {}, Size: {}, PatientId: {}", pageNo, pageSize, patientId);
        PaginatedPayload<SuggestionResponse> suggestions = suggestionService.getAllSuggestions(patientId, pageNo, pageSize);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Suggestions retrieved successfully.", suggestions));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get suggestion by ID", description = "Retrieves a suggestion's details by database ID.")
    public ResponseEntity<ApiResponse<SuggestionResponse>> getSuggestionById(@PathVariable Long id) {
        log.debug("API call: Fetching suggestion with ID: {}", id);
        SuggestionResponse response = suggestionService.getSuggestionById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Suggestion retrieved successfully.", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update suggestion by ID", description = "Updates an existing suggestion's details.")
    public ResponseEntity<ApiResponse<SuggestionResponse>> updateSuggestionById(
            @PathVariable Long id,
            @RequestBody @Valid UpdateSuggestionRequest request
    ) {
        log.debug("API call: Updating suggestion with ID: {}", id);
        SuggestionResponse updatedSuggestion = suggestionService.updateSuggestionById(id, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Suggestion updated successfully.", updatedSuggestion));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete suggestion by ID", description = "Deletes a suggestion record based on ID.")
    public ResponseEntity<Void> deleteSuggestionById(@PathVariable Long id) {
        log.debug("API call: Deleting suggestion with ID: {}", id);
        suggestionService.deleteSuggestionById(id);
        return ResponseEntity.noContent().build();
    }
}
