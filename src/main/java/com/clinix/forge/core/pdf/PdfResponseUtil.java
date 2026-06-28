package com.clinix.forge.core.pdf;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

public class PdfResponseUtil {

    public static ResponseEntity<byte[]> inline(byte[] pdf, String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(pdf.length))
                .body(pdf);
    }
}
