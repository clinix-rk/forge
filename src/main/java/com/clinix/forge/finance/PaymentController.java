package com.clinix.forge.finance;

import com.clinix.forge.core.payload.ApiResponse;
import com.clinix.forge.core.payload.PaginatedPayload;
import com.clinix.forge.finance.dto.*;
import com.clinix.forge.finance.service.Form3CPdfService;
import com.clinix.forge.finance.service.PaymentService;
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

import com.clinix.forge.core.pdf.PdfResponseUtil;
import com.clinix.forge.finance.entity.PaymentMethod;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Slf4j
@Validated
@RestController
@RequestMapping("/finance")
@RequiredArgsConstructor
@Tag(name = "Finance Management", description = "Endpoints for managing receipts and payments")
public class FinanceController {

    private final PaymentService paymentService;
    private final Form3CPdfService form3CPdfService;

    // --- Payment Endpoints ---

    @PostMapping("/payments")
    @Operation(summary = "Register a payment", description = "Creates a new payment record associated with a receipt and treatment.")
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @RequestBody @Valid CreatePaymentRequest request
    ) {
        log.debug("API call: Create payment");
        PaymentResponse response = paymentService.createPayment(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment registered successfully.", response));
    }

    @GetMapping("/payments")
    @Operation(summary = "Get payments (Paginated)", description = "Retrieves a paginated list of all payments.")
    public ResponseEntity<ApiResponse<PaginatedPayload<PaymentResponse>>> getAllPayments(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number must be greater than or equal to 0.") int pageNo,
            @RequestParam(defaultValue = "10") @Min(value = 5, message = "Page size must be at least 5.") @Max(value = 1000, message = "Page size must be less than or equal to 1000.") int pageSize,
            @RequestParam(required = false) Long patientId
    ) {
        log.debug("API call: Fetching payments paginated - Page: {}, Size: {}, PatientId: {}", pageNo, pageSize, patientId);
        PaginatedPayload<PaymentResponse> response = paymentService.getAllPayments(patientId, pageNo, pageSize);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Payments retrieved successfully.", response));
    }

    @GetMapping("/payments/{id}")
    @Operation(summary = "Get payment by ID", description = "Retrieves a payment's details by ID.")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(@PathVariable Long id) {
        log.debug("API call: Fetching payment with ID: {}", id);
        PaymentResponse response = paymentService.getPaymentById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Payment retrieved successfully.", response));
    }

    @PutMapping("/payments/{id}")
    @Operation(summary = "Update payment by ID", description = "Updates an existing payment details.")
    public ResponseEntity<ApiResponse<PaymentResponse>> updatePaymentById(
            @PathVariable Long id,
            @RequestBody @Valid UpdatePaymentRequest request
    ) {
        log.debug("API call: Updating payment with ID: {}", id);
        PaymentResponse response = paymentService.updatePaymentById(id, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Payment updated successfully.", response));
    }

    @DeleteMapping("/payments/{id}")
    @Operation(summary = "Delete payment by ID", description = "Deletes a payment record based on ID.")
    public ResponseEntity<Void> deletePaymentById(@PathVariable Long id) {
        log.debug("API call: Deleting payment with ID: {}", id);
        paymentService.deletePaymentById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Receipt Endpoints ---

    @PostMapping("/recipts")
    @Operation(summary = "Create a receipt", description = "Creates a new receipt (spelled recipt to match database).")
    public ResponseEntity<ApiResponse<ReceiptResponse>> createRecipt(
            @RequestBody @Valid CreateReceiptRequest request
    ) {
        log.debug("API call: Create recipt");
        ReceiptResponse response = reciptService.createReceipt(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Recipt created successfully.", response));
    }

    @GetMapping("/recipts")
    @Operation(summary = "Get receipts (Paginated)", description = "Retrieves a paginated list of all receipts.")
    public ResponseEntity<ApiResponse<PaginatedPayload<ReceiptResponse>>> getAllRecipts(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number must be greater than or equal to 0.") int pageNo,
            @RequestParam(defaultValue = "10") @Min(value = 5, message = "Page size must be at least 5.") @Max(value = 1000, message = "Page size must be less than or equal to 1000.") int pageSize,
            @RequestParam(required = false) Long patientId
    ) {
        log.debug("API call: Fetching recipts paginated - Page: {}, Size: {}, PatientId: {}", pageNo, pageSize, patientId);
        PaginatedPayload<ReceiptResponse> response = reciptService.getAllRecipts(patientId, pageNo, pageSize);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Recipts retrieved successfully.", response));
    }

    @GetMapping("/recipts/{id}")
    @Operation(summary = "Get receipt by ID", description = "Retrieves a receipt's details by ID.")
    public ResponseEntity<ApiResponse<ReceiptResponse>> getReciptById(@PathVariable Long id) {
        log.debug("API call: Fetching recipt with ID: {}", id);
        ReceiptResponse response = reciptService.getReciptById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Recipt retrieved successfully.", response));
    }

    @PutMapping("/recipts/{id}")
    @Operation(summary = "Update receipt by ID", description = "Updates an existing receipt details.")
    public ResponseEntity<ApiResponse<ReceiptResponse>> updateReciptById(
            @PathVariable Long id,
            @RequestBody @Valid UpdateReceiptRequest request
    ) {
        log.debug("API call: Updating recipt with ID: {}", id);
        ReceiptResponse response = reciptService.updateReciptById(id, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Recipt updated successfully.", response));
    }

    @DeleteMapping("/recipts/{id}")
    @Operation(summary = "Delete receipt by ID", description = "Deletes a receipt record based on ID.")
    public ResponseEntity<Void> deleteReciptById(@PathVariable Long id) {
        log.debug("API call: Deleting recipt with ID: {}", id);
        reciptService.deleteReciptById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recipts/{id}/pdf")
    @Operation(
        summary = "Generate Receipt PDF",
        description = "Generates a printable PDF receipt covering all treatments linked to the given receipt ID."
    )
    public ResponseEntity<byte[]> getReceiptPdf(@PathVariable Long id) {
        log.info("API call: Generate receipt PDF for ID: {}", id);
        byte[] pdf = reciptService.generateReceiptPdf(id);
        return PdfResponseUtil.inline(pdf, "receipt-" + id + ".pdf");
    }

    @GetMapping("/form3c/pdf")
    @Operation(
        summary = "Generate Form 3C PDF",
        description = "Generates the standard Indian dental Form 3C patient register for a date range. Pass doctorId to generate a per-doctor report."
    )
    public ResponseEntity<byte[]> getForm3CPdf(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
        @RequestParam(required = false) Long doctorId
    ) {
        log.info("API call: Generate Form 3C from {} to {}, doctorId={}", fromDate, toDate, doctorId);
        byte[] pdf = form3CPdfService.generateForm3CPdf(fromDate, toDate, doctorId);
        return PdfResponseUtil.inline(pdf, "form3c-" + fromDate + "-to-" + toDate + ".pdf");
    }

    @GetMapping("/payments/enriched")
    @Operation(summary = "Get enriched payments (Paginated)", description = "Retrieves a list of payments joined with patient info (name, caseNo) and filtered by criteria.")
    public ResponseEntity<ApiResponse<PaginatedPayload<EnrichedPaymentResponse>>> getEnrichedPayments(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "Page number must be at least 1.") int pageNo,
            @RequestParam(defaultValue = "10") @Min(value = 5, message = "Page size must be at least 5.") @Max(value = 1000, message = "Page size must be less than or equal to 1000.") int pageSize,
            @RequestParam(required = false) PaymentMethod method,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String search
    ) {
        log.debug("API call: Enriched payments lookup - Page: {}, Size: {}, Method: {}, FromDate: {}, ToDate: {}, Search: {}",
                pageNo, pageSize, method, fromDate, toDate, search);
        PaginatedPayload<EnrichedPaymentResponse> response = paymentService.getEnrichedPayments(
                pageNo, pageSize, method, fromDate, toDate, search);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Enriched payments retrieved successfully.", response));
    }
}

