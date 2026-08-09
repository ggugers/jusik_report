package com.example.jusikreport.invest.dto;

import com.example.jusikreport.invest.domain.InvestReport;

import java.time.LocalDate;

public record InvestReportListItem(
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
    public static InvestReportListItem from(InvestReport report) {
        return new InvestReportListItem(
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
