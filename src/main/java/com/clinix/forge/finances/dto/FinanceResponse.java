package com.clinix.forge.finances.dto;

import java.time.LocalDate;

public record FinanceResponse(
        Long id,
        Long patientId,
        String caseNo,
        LocalDate date,
        String patientName,
        String treatmentDetails,
        Double amount,
        String method,
        LocalDate receivedDate,
        String receiptNo
) {
}
