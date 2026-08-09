package com.example.jusikreport.report.dto;

import com.example.jusikreport.report.domain.Report;
import com.example.jusikreport.report.domain.TargetPriceChange;

import java.time.LocalDate;

public record ReportListItem(
        Long id,
        boolean read,
        boolean important,
        TargetPriceChange targetPriceChange,
        String stockCode,
        String stockName,
        String stockDetailUrl,
        String marketType,
        Integer marketCapRank,
        String marketCapText,
        Long marketCapValue,
        String reportTitle,
        String securitiesFirm,
        String pdfUrl,
        String detailUrl,
        LocalDate reportDate
) {
    public static ReportListItem from(Report report, Integer marketCapRank, String marketCapText, Long marketCapValue) {
        return new ReportListItem(
                report.getId(),
                report.isRead(),
                report.isImportant(),
                report.getTargetPriceChange(),
                report.getStockCode(),
                report.getStockName(),
                report.getStockCode() == null ? null : "https://finance.naver.com/item/fchart.naver?code=" + report.getStockCode(),
                null,
                marketCapRank,
                marketCapText,
                marketCapValue,
                report.getReportTitle(),
                report.getSecuritiesFirm(),
                report.getPdfUrl(),
                report.getDetailUrl(),
                report.getReportDate()
        );
    }

    public ReportListItem withMarket(String marketType, Integer marketCapRank, String marketCapText, Long marketCapValue) {
        return new ReportListItem(
                id,
                read,
                important,
                targetPriceChange,
                stockCode,
                stockName,
                stockDetailUrl,
                marketType,
                marketCapRank,
                marketCapText,
                marketCapValue,
                reportTitle,
                securitiesFirm,
                pdfUrl,
                detailUrl,
                reportDate
        );
    }
}
