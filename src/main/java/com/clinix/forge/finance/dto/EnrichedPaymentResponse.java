package com.clinix.forge.finance.dto;

import com.clinix.forge.finance.entity.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Represents payment details joined with patient identity information")
public record EnrichedPaymentResponse(
        @Schema(description = "Unique ID of the payment", example = "1")
        Long id,

        @Schema(description = "Unique ID of the associated receipt", example = "2")
        Long reciptId,

        @Schema(description = "Unique ID of the associated treatment", example = "3")
        Long treatmentId,

        @Schema(description = "Unique ID of the associated patient", example = "5")
        Long patientId,

        @Schema(description = "Full name of the patient", example = "John Doe")
        String patientName,

        @Schema(description = "Case number of the patient", example = "D00001")
        String patientCaseNo,

        @Schema(description = "The amount paid", example = "1500.00")
        Double amount,

        @Schema(description = "Payment method used (CASH, ONLINE, CHEQUE)")
        PaymentMethod method,

        @Schema(description = "Transaction reference or check number", example = "TXN987654321")
        String reference,

        @Schema(description = "Timestamp when the payment record was created")
        Instant createdAt,

        @Schema(description = "Timestamp when the payment record was last updated")
        Instant updatedAt
) {}
