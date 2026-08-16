package com.clinix.forge.core.pdf.dto;

import java.util.List;

public record PrescriptionPdfData(
        String caseNo,
        String patientName,
        String date,
        String age,
        String gender,
        String details,
        List<PrescriptionMedicineItem> medicines,
        String doctorName,
        String treatmentDetail
) {
}
