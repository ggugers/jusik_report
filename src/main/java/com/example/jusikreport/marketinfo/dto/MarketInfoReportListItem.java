package com.example.jusikreport.marketinfo.dto;

import com.example.jusikreport.marketinfo.domain.MarketInfoReport;

import java.time.LocalDate;

public record MarketInfoReportListItem(
        Long id,
        boolean read,
        boolean important,
        String reportTitle,
        String securitiesFirm,
        String pdfUrl,
        String detailUrl,
        LocalDate reportDate,
        Integer viewCount
) {
    public static MarketInfoReportListItem from(MarketInfoReport report) {
        return new MarketInfoReportListItem(
                report.getId(),
                report.isRead(),
                report.isImportant(),
                report.getReportTitle(),
                report.getSecuritiesFirm(),
                report.getPdfUrl(),
                report.getDetailUrl(),
                report.getReportDate(),
                report.getViewCount()
        );
    }
}
