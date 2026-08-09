package com.example.jusikreport.industry.dto;

import com.example.jusikreport.industry.domain.IndustryReport;

import java.time.LocalDate;

public record IndustryReportListItem(
        Long id,
        boolean read,
        boolean important,
        String categoryName,
        String reportTitle,
        String securitiesFirm,
        String pdfUrl,
        String detailUrl,
        LocalDate reportDate
) {
    public static IndustryReportListItem from(IndustryReport report) {
        return new IndustryReportListItem(
                report.getId(),
                report.isRead(),
                report.isImportant(),
                report.getCategoryName(),
                report.getReportTitle(),
                report.getSecuritiesFirm(),
                report.getPdfUrl(),
                report.getDetailUrl(),
                report.getReportDate()
        );
    }
}
