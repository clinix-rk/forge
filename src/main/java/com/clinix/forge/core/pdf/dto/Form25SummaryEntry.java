package com.clinix.forge.core.pdf.dto;

public record Form25SummaryEntry(
        String date,
        String cashAmount,
        String chequeOnlineAmount,
        String totalAmount
) {
}
