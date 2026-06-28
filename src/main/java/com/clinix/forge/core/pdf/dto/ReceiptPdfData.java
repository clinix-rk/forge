package com.clinix.forge.core.pdf.dto;

import java.util.List;

public record ReceiptPdfData(
    String doctorName,
    List<ReceiptLineItem> lines,
    Double total
) {}
