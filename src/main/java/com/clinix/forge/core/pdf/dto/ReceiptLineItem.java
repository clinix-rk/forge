package com.clinix.forge.core.pdf.dto;

public record ReceiptLineItem(
    String date,       // "dd/MM/yy"
    String workDone,   // category name + treatment details
    Double amount
) {}
