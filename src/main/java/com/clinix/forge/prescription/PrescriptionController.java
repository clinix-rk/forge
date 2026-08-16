package com.clinix.forge.prescription;


import com.clinix.forge.core.payload.ApiResponse;
import com.clinix.forge.core.payload.PaginationMetadata;
import com.clinix.forge.core.pdf.PdfResponseUtil;
import com.clinix.forge.prescription.dto.CreatePrescriptionRequest;
import com.clinix.forge.prescription.dto.PdfData;
import com.clinix.forge.prescription.dto.PrescriptionResponse;
import com.clinix.forge.prescription.dto.UpdatePrescriptionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
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
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Validated
@RestController
@RequestMapping("/patients/{patientId}/prescriptions")
@RequiredArgsConstructor
@Tag(name = "Prescription Management", description = "Endpoints for managing prescriptions, medicines, and drug dosages")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping
    @Operation(summary = "Create a prescription", description = "Creates a new patient prescription.")
    public ResponseEntity<ApiResponse<PrescriptionResponse>> createPrescription(
            @PathVariable Long patientId,
            @RequestBody @Valid CreatePrescriptionRequest request
    ) {
        log.debug("API call: Create prescription");
        PrescriptionResponse response = prescriptionService.createPrescription(patientId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Get prescriptions (Paginated)", description = "Retrieves a paginated list of all prescriptions.")
    public ResponseEntity<ApiResponse<java.util.List<PrescriptionResponse>>> getAllPrescriptions(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number must be greater than or equal to 0.") int pageNo,
            @RequestParam(defaultValue = "10") @Min(value = 5, message = "Page size must be at least 5.") @Max(value = 1000, message = "Page size must be less than or equal to 1000.") int pageSize,
            @PathVariable(required = false) Long patientId
    ) {
        log.debug("API call: Fetching prescriptions paginated - Page: {}, Size: {}, PatientId: {}", pageNo, pageSize, patientId);
        Page<PrescriptionResponse> response = prescriptionService.getAllPrescriptions(patientId, pageNo, pageSize);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response.getContent(), new PaginationMetadata(response.getNumber(), response.getSize(), response.getTotalElements(), response.getTotalPages(), response.hasNext(), response.hasPrevious())));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get prescription by ID", description = "Retrieves a prescription's details by ID.")
    public ResponseEntity<ApiResponse<PrescriptionResponse>> getPrescriptionById(
            @PathVariable Long patientId,
            @PathVariable Long id
    ) {
        log.debug("API call: Fetching prescription with ID: {}", id);
        PrescriptionResponse response = prescriptionService.getPrescriptionById(patientId, id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update prescription by ID", description = "Updates an existing prescription record.")
    public ResponseEntity<ApiResponse<PrescriptionResponse>> updatePrescriptionById(
            @PathVariable Long id,
            @RequestBody @Valid UpdatePrescriptionRequest request
    ) {
        log.debug("API call: Updating prescription with ID: {}", id);
        PrescriptionResponse response = prescriptionService.updatePrescriptionById(id, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete prescription by ID", description = "Deletes a prescription record based on ID.")
    public ResponseEntity<Void> deletePrescriptionById(@PathVariable Long id) {
        log.debug("API call: Deleting prescription with ID: {}", id);
        prescriptionService.deletePrescriptionById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/pdf")
    @Operation(
            summary = "Generate Prescription Print-Fill PDF",
            description = "Generates a content-only PDF for printing on the pre-printed Aditya Dental Clinic letterhead pad. The physical paper provides the header, watermark, footer, and QR code."
    )
    public ResponseEntity<byte[]> getPrescriptionPdf(
            @PathVariable Long id,
            @RequestParam String referralType,
            @Nullable @RequestBody PdfData data) {

        log.info("API call: Generate prescription PDF for ID: {}, referralType: {}", id, referralType);

        // Enforce conditional requirement
        if (isDataRequired(referralType) && data == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Request body 'data' is required when referralType is 'extended' or 'standard'."
            );
        }

        byte[] pdf = prescriptionService.generatePrescriptionPdf(id, referralType, data);

        return PdfResponseUtil.inline(pdf, "prescription.pdf");
    }

    private boolean isDataRequired(String referralType) {
        return "extended".equalsIgnoreCase(referralType) || "standard".equalsIgnoreCase(referralType);
    }
}
