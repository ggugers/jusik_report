package com.example.jusikreport.marketinfo.dto;

import java.time.LocalDate;

public record MarketInfoReportSearchCondition(
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
