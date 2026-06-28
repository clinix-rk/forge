package com.clinix.forge.storage;

import com.clinix.forge.core.payload.ApiResponse;
import com.clinix.forge.core.payload.PaginatedPayload;
import com.clinix.forge.storage.dto.CreateFileRequest;
import com.clinix.forge.storage.dto.FileResponse;
import com.clinix.forge.storage.dto.UpdateFileRequest;
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
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Validated
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Tag(name = "File Storage Management", description = "Endpoints for managing patient file uploads")
public class FileController {

    private final FileService fileService;

    @PostMapping
    @Operation(summary = "Register a file", description = "Registers metadata for an uploaded patient file.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "File registered successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Patient not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "File with same patient, name or location already exists")
    })
    public ResponseEntity<ApiResponse<FileResponse>> createFile(
            @RequestBody @Valid CreateFileRequest request
    ) {
        log.debug("creating file record");
        FileResponse response = fileService.createFile(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("File registered successfully.", response));
    }

    @GetMapping
    @Operation(summary = "Get files (Paginated)", description = "Retrieves a paginated list of all file metadata records.")
    public ResponseEntity<ApiResponse<PaginatedPayload<FileResponse>>> getAllFiles(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number must be greater than or equal to 0.") int pageNo,
            @RequestParam(defaultValue = "10") @Min(value = 5, message = "Page size must be at least 5.") @Max(value = 1000, message = "Page size must be less than or equal to 1000.") int pageSize
    ) {
        log.debug("API call: Fetching files paginated - Page: {}, Size: {}", pageNo, pageSize);
        PaginatedPayload<FileResponse> files = fileService.getAllFiles(pageNo, pageSize);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Files retrieved successfully.", files));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get file by ID", description = "Retrieves file metadata details by database ID.")
    public ResponseEntity<ApiResponse<FileResponse>> getFileById(@PathVariable Long id) {
        log.debug("API call: Fetching file with ID: {}", id);
        FileResponse file = fileService.getFileById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("File retrieved successfully.", file));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update file by ID", description = "Updates an existing file metadata record.")
    public ResponseEntity<ApiResponse<FileResponse>> updateFileById(
            @PathVariable Long id,
            @RequestBody @Valid UpdateFileRequest request
    ) {
        log.debug("API call: Updating file with ID: {}", id);
        FileResponse updatedFile = fileService.updateFileById(id, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("File updated successfully.", updatedFile));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete file by ID", description = "Deletes a file metadata record based on ID.")
    public ResponseEntity<Void> deleteFileById(@PathVariable Long id) {
        log.debug("API call: Deleting file with ID: {}", id);
        fileService.deleteFileById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload patient PDF", description = "Uploads a patient PDF document (max 10MB). Enforces single PDF per patient constraint.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "File uploaded successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "File is empty, not a PDF, or too large (limit 10MB)")
    })
    public ResponseEntity<ApiResponse<FileResponse>> uploadFile(
            @RequestParam("patientId") Long patientId,
            @RequestParam("file") MultipartFile file
    ) {
        log.info("API call: Uploading PDF for patient ID: {}", patientId);
        FileResponse response = fileService.uploadPatientPdf(patientId, file);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("File uploaded successfully.", response));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get file by Patient ID", description = "Retrieves file metadata details by patient ID.")
    public ResponseEntity<ApiResponse<FileResponse>> getFileByPatientId(@PathVariable Long patientId) {
        log.debug("API call: Fetching file with Patient ID: {}", patientId);
        FileResponse file = fileService.getFileByPatientId(patientId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("File retrieved successfully.", file));
    }

    @GetMapping("/patient/{patientId}/pdf")
    @Operation(summary = "Download patient PDF", description = "Downloads the patient's uploaded PDF document.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "File downloaded successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No file found for patient")
    })
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long patientId) {
        log.info("API call: Downloading PDF for patient ID: {}", patientId);
        byte[] data = fileService.downloadPatientPdf(patientId);
        String name = fileService.getPatientPdfName(patientId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + name + "\"")
                .body(data);
    }
}

