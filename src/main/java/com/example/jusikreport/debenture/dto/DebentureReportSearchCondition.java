package com.example.jusikreport.debenture.dto;

import java.time.LocalDate;

public record DebentureReportSearchCondition(
        LocalDate startDate,
        LocalDate endDate,
        String reportTitle,
        String securitiesFirm,
        Boolean read,
        Boolean important
) {
    public String normalizedReportTitle() {
        return reportTitle == null ? null : reportTitle.trim();
    }

    public String normalizedSecuritiesFirm() {
        return securitiesFirm == null ? null : securitiesFirm.trim();
    }
}
