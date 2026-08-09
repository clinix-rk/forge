package com.clinix.forge.core.pdf.dto;

public record PrescriptionMedicineItem(
        String medicineName,
        String dosage,
        String instruction,
        Integer quantity
) {
}
