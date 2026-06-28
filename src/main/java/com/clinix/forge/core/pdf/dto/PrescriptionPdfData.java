package com.clinix.forge.core.pdf.dto;

import java.util.List;

public record PrescriptionPdfData(
    String patientName,
    String date,          // "dd/MM/yyyy"
    String ageGender,     // "34 yrs / Male"
    String details,       // free text details
    List<PrescriptionMedicineItem> medicines
) {}
