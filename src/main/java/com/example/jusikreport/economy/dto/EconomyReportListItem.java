package com.example.jusikreport.economy.dto;

import com.example.jusikreport.economy.domain.EconomyReport;

import java.time.LocalDate;

public record EconomyReportListItem(
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
    public static EconomyReportListItem from(EconomyReport report) {
        return new EconomyReportListItem(
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
