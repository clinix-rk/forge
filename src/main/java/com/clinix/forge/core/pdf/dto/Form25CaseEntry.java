package com.clinix.forge.core.pdf.dto;

public record Form25CaseEntry(
        String caseNo,
        String date,
        String patientName,
        String serviceRendered,
        String feesReceived,
        String receiptDate
) {
}
