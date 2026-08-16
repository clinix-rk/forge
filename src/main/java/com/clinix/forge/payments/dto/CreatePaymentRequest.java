package com.clinix.forge.payments.dto;

import com.clinix.forge.payments.entity.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for registering a payment")
public record CreatePaymentRequest(
        @NotNull(message = "Treatment ID is required")
        @Positive(message = "Treatment ID must be a positive number")
        @Schema(description = "Unique ID of the associated treatment", example = "3")
        Long treatmentId,

        @NotNull(message = "Payment amount is required")
        @Positive(message = "Amount must be a positive number")
        @Schema(description = "The amount paid", example = "1500.00")
        Double amount,

        @NotNull(message = "Payment method is required")
        @Schema(description = "Payment method used (CASH, ONLINE, CHEQUE)")
        PaymentMethod method,

        @Size(max = 255, message = "Reference must not exceed 255 characters")
        @Schema(description = "Optional transaction reference or check number", example = "TXN987654321")
        String reference
) {
}
