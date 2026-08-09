package com.clinix.forge.core.pdf.dto;

public record Form3CEntry(
        Integer recNo,
        String caseNumber,
        String patientName,
        String treatmentGiven,
        Double amount         // 0.0 if no payment recorded yet
) {
}
