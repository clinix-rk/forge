package com.clinix.forge.core.pdf.dto;

import java.util.List;

public record Form3CData(
        String fromDate,
        String toDate,
        String doctorName,    // null if clinic-wide
        int pageNo,
        List<Form3CDateGroup> groups,
        Double grandTotal
) {
}
