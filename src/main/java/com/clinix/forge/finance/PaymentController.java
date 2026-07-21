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
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/{patientId}/finance")
@RequiredArgsConstructor
@Tag(name = "Finance Management", description = "Endpoints for managing receipts and payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final Form3CPdfService form3CPdfService;

    @PostMapping("/payments")
    @Operation(summary = "Add a payment", description = "Adds a new payment record to related to a patient")
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @PathVariable
            Long patientId,

            @RequestBody
            @Valid
            CreatePaymentRequest request
    ) {
        log.debug("Serving add payment request");
        PaymentResponse response = paymentService.createPayment(patientId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Successfully added payment", response));
    }

    @GetMapping("/payments")
    @Operation(summary = "Get payments (Paginated)", description = "Retrieves a paginated list of all payments.")
    public ResponseEntity<ApiResponse<PaginatedPayload<PaymentResponse>>> getAllPayments(
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page number cannot be smaller than 0")
            int pageNo,

            @RequestParam(defaultValue = "10")
            @Min(value = 5, message = "Page number size can not be smaller than 5")
            @Max(value = 1000, message = "Page size must be less than or equal to 1000.")
            int pageSize,

            @PathVariable
            Long patientId
    ) {
        log.debug("Fetching {} payments for page no : {}", pageSize, pageNo);
        PaginatedPayload<PaymentResponse> response = paymentService.getAllPayments(patientId, pageNo, pageSize);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Payments retrieved successfully.", response));
    }

    @GetMapping("/payments/{id}")
    @Operation(summary = "Get payment information using the payment id", description = "Returns payment details for payment with given id")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(
            @PathVariable
            Long patientId,

            @PathVariable
            Long paymentId
    ) {
        log.debug("Fetching payment details for payment: {}", paymentId);
        PaymentResponse response = paymentService.getPaymentById(patientId, paymentId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Payment retrieved successfully.", response));
    }

    @PutMapping("/payments/{id}")
    @Operation(summary = "Update payment by ID", description = "Updates an existing payment details.")
    public ResponseEntity<ApiResponse<PaymentResponse>> updatePaymentById(
            @PathVariable
            Long id,

            @PathVariable
            Long patientId,

            @RequestBody
            @Valid
            UpdatePaymentRequest request
    ) {
        log.debug("Updating payment id : {}", id);
        PaymentResponse response = paymentService.updatePaymentById(patientId, id, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Payment updated successfully.", response));
    }

    @DeleteMapping("/payments/{id}")
    @Operation(summary = "Delete payment by ID", description = "Deletes a payment record based on ID.")
    public ResponseEntity<Void> deletePaymentById(
            @PathVariable Long id,
            @PathVariable Long patientId
    ) {
        log.debug("Deleting payment id : {}", id);
        paymentService.deletePaymentById(patientId, id);
        return ResponseEntity.noContent().build();
    }
}