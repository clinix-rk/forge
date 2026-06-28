package com.clinix.forge.prescription;

import com.clinix.forge.core.payload.ApiResponse;
import com.clinix.forge.core.payload.PaginatedPayload;
import com.clinix.forge.prescription.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.clinix.forge.core.pdf.PdfResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/prescriptions")
@RequiredArgsConstructor
@Tag(name = "Prescription Management", description = "Endpoints for managing prescriptions, medicines, and drug dosages")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final MedicineService medicineService;
    private final DrugDosageService drugDosageService;

    // --- Prescription Endpoints ---

    @PostMapping
    @Operation(summary = "Create a prescription", description = "Creates a new patient prescription.")
    public ResponseEntity<ApiResponse<PrescriptionResponse>> createPrescription(
            @RequestBody @Valid CreatePrescriptionRequest request
    ) {
        log.debug("API call: Create prescription");
        PrescriptionResponse response = prescriptionService.createPrescription(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Prescription created successfully.", response));
    }

    @GetMapping
    @Operation(summary = "Get prescriptions (Paginated)", description = "Retrieves a paginated list of all prescriptions.")
    public ResponseEntity<ApiResponse<PaginatedPayload<PrescriptionResponse>>> getAllPrescriptions(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number must be greater than or equal to 0.") int pageNo,
            @RequestParam(defaultValue = "10") @Min(value = 5, message = "Page size must be at least 5.") @Max(value = 1000, message = "Page size must be less than or equal to 1000.") int pageSize,
            @RequestParam(required = false) Long patientId
    ) {
        log.debug("API call: Fetching prescriptions paginated - Page: {}, Size: {}, PatientId: {}", pageNo, pageSize, patientId);
        PaginatedPayload<PrescriptionResponse> response = prescriptionService.getAllPrescriptions(patientId, pageNo, pageSize);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Prescriptions retrieved successfully.", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get prescription by ID", description = "Retrieves a prescription's details by ID.")
    public ResponseEntity<ApiResponse<PrescriptionResponse>> getPrescriptionById(@PathVariable Long id) {
        log.debug("API call: Fetching prescription with ID: {}", id);
        PrescriptionResponse response = prescriptionService.getPrescriptionById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Prescription retrieved successfully.", response));
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
                .body(ApiResponse.success("Prescription updated successfully.", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete prescription by ID", description = "Deletes a prescription record based on ID.")
    public ResponseEntity<Void> deletePrescriptionById(@PathVariable Long id) {
        log.debug("API call: Deleting prescription with ID: {}", id);
        prescriptionService.deletePrescriptionById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/pdf")
    @Operation(
        summary = "Generate Prescription Print-Fill PDF",
        description = "Generates a content-only PDF for printing on the pre-printed Aditya Dental Clinic letterhead pad. The physical paper provides the header, watermark, footer, and QR code."
    )
    public ResponseEntity<byte[]> getPrescriptionPdf(@PathVariable Long id) {
        log.info("API call: Generate prescription PDF for ID: {}", id);
        byte[] pdf = prescriptionService.generatePrescriptionPdf(id);
        return PdfResponseUtil.inline(pdf, "prescription-" + id + ".pdf");
    }

    // --- Medicine Endpoints ---

    @PostMapping("/medicines")
    @Operation(summary = "Add a medicine", description = "Creates a new medicine catalog item.")
    public ResponseEntity<ApiResponse<MedicineResponse>> createMedicine(
            @RequestBody @Valid CreateMedicineRequest request
    ) {
        log.debug("API call: Create new medicine catalog entry");
        MedicineResponse response = medicineService.createMedicine(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Medicine added to catalog successfully.", response));
    }

    @GetMapping("/medicines")
    @Operation(summary = "Get medicines (Paginated)", description = "Retrieves a paginated list of all medicine items.")
    public ResponseEntity<ApiResponse<PaginatedPayload<MedicineResponse>>> getAllMedicines(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number must be greater than or equal to 0.") int pageNo,
            @RequestParam(defaultValue = "10") @Min(value = 5, message = "Page size must be at least 5.") @Max(value = 1000, message = "Page size must be less than or equal to 1000.") int pageSize
    ) {
        log.debug("API call: Fetching medicines paginated - Page: {}, Size: {}", pageNo, pageSize);
        PaginatedPayload<MedicineResponse> response = medicineService.getAllMedicines(pageNo, pageSize);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Medicines retrieved successfully.", response));
    }

    @GetMapping("/medicines/{id}")
    @Operation(summary = "Get medicine by ID", description = "Retrieves a medicine's details by ID.")
    public ResponseEntity<ApiResponse<MedicineResponse>> getMedicineById(@PathVariable Long id) {
        log.debug("API call: Fetching medicine with ID: {}", id);
        MedicineResponse response = medicineService.getMedicineById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Medicine retrieved successfully.", response));
    }

    @PutMapping("/medicines/{id}")
    @Operation(summary = "Update medicine by ID", description = "Updates an existing medicine details.")
    public ResponseEntity<ApiResponse<MedicineResponse>> updateMedicineById(
            @PathVariable Long id,
            @RequestBody @Valid UpdateMedicineRequest request
    ) {
        log.debug("API call: Updating medicine with ID: {}", id);
        MedicineResponse response = medicineService.updateMedicineById(id, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Medicine updated successfully.", response));
    }

    @DeleteMapping("/medicines/{id}")
    @Operation(summary = "Delete medicine by ID", description = "Deletes a medicine catalog entry based on ID.")
    public ResponseEntity<Void> deleteMedicineById(@PathVariable Long id) {
        log.debug("API call: Deleting medicine with ID: {}", id);
        medicineService.deleteMedicineById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Drug Dosage Endpoints ---

    @PostMapping("/dosages")
    @Operation(summary = "Add a drug dosage pattern", description = "Creates a new drug dosage pattern.")
    public ResponseEntity<ApiResponse<DrugDosageResponse>> createDrugDosage(
            @RequestBody @Valid CreateDrugDosageRequest request
    ) {
        log.debug("API call: Create new drug dosage pattern");
        DrugDosageResponse response = drugDosageService.createDrugDosage(request);
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
        PaginatedPayload<DrugDosageResponse> response = drugDosageService.getAllDrugDosages(pageNo, pageSize);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Drug dosages retrieved successfully.", response));
    }

    @GetMapping("/dosages/{id}")
    @Operation(summary = "Get drug dosage by ID", description = "Retrieves a drug dosage pattern's details by ID.")
    public ResponseEntity<ApiResponse<DrugDosageResponse>> getDrugDosageById(@PathVariable Long id) {
        log.debug("API call: Fetching dosage with ID: {}", id);
        DrugDosageResponse response = drugDosageService.getDrugDosageById(id);
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
        DrugDosageResponse response = drugDosageService.updateDrugDosageById(id, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Drug dosage updated successfully.", response));
    }

    @DeleteMapping("/dosages/{id}")
    @Operation(summary = "Delete drug dosage by ID", description = "Deletes a drug dosage pattern based on ID.")
    public ResponseEntity<Void> deleteDrugDosageById(@PathVariable Long id) {
        log.debug("API call: Deleting dosage with ID: {}", id);
        drugDosageService.deleteDrugDosageById(id);
        return ResponseEntity.noContent().build();
    }
}
