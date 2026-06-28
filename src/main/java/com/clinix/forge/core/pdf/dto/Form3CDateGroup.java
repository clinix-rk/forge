package com.clinix.forge.core.pdf.dto;

import java.util.List;

public record Form3CDateGroup(
    String dateLabel,
    List<Form3CEntry> entries,
    Double dailyTotal
) {}
