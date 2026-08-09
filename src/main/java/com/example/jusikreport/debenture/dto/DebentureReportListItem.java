package com.example.jusikreport.debenture.dto;

import com.example.jusikreport.debenture.domain.DebentureReport;

import java.time.LocalDate;

public record DebentureReportListItem(
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
    public static DebentureReportListItem from(DebentureReport report) {
        return new DebentureReportListItem(
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
