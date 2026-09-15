package com.clinix.forge.prescription.types;

public record PrescriptionPdfResponse(
        byte[] pdf,
        String name
) {
}
