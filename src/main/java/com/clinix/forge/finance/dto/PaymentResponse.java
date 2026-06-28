package com.clinix.forge.finance.dto;

import com.clinix.forge.finance.entity.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Represents payment transaction details in the system")
public record PaymentResponse(
        @Schema(description = "Unique ID of the payment", example = "1")
        Long id,

        @Schema(description = "Unique ID of the associated receipt", example = "2")
        Long reciptId,

        @Schema(description = "Unique ID of the associated treatment", example = "3")
        Long treatmentId,

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
