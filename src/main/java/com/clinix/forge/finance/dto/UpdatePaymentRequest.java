package com.clinix.forge.finance.dto;

import com.clinix.forge.finance.entity.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for updating a payment's details")
public record UpdatePaymentRequest(
        @NotNull(message = "Payment amount is required")
        @Positive(message = "Amount must be a positive number")
        @Schema(description = "The updated amount paid", example = "1500.00")
        Double amount,

        @NotNull(message = "Payment method is required")
        @Schema(description = "Updated payment method (CASH, ONLINE, CHEQUE)")
        PaymentMethod method,

        @Size(max = 255, message = "Reference must not exceed 255 characters")
        @Schema(description = "Updated transaction reference or check number", example = "TXN987654321")
        String reference
) {
}
