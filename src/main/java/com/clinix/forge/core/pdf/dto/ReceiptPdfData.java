package com.clinix.forge.core.pdf.dto;

public record ReceiptPdfData(
        String receiptNo,
        String date,
        String patientName,
        String treatmentDetail,
        String amount,
        String amountInWords,
        String method,
        Boolean printStamp
) {
}
