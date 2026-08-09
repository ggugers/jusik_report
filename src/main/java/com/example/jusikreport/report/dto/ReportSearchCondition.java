package com.example.jusikreport.report.dto;

import java.time.LocalDate;

import com.example.jusikreport.report.domain.TargetPriceChange;

public record ReportSearchCondition(
        LocalDate startDate,
        LocalDate endDate,
        String stockName,
        String securitiesFirm,
        Boolean read,
        Boolean important,
        TargetPriceChange targetPriceChange
) {
    public String normalizedStockName() {
        return stockName == null ? null : stockName.trim();
    }

    public String normalizedSecuritiesFirm() {
        return securitiesFirm == null ? null : securitiesFirm.trim();
    }
}
