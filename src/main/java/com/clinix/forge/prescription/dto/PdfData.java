package com.clinix.forge.prescription.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request body payload for the pdf generation for prescriptions")
public record PdfData(

        @Schema(description = "Name of the doctor being referred to", example = "Dr. John Doe")
        String doctorName,

        @Schema(description = "Details of the treatment to be performed", example = "RCT")
        String treatmentDetail
) {
}
