package com.example.jusikreport.report.dto;

import java.time.LocalDate;

public record CrawledReport(
        String stockCode,
        String stockName,
        String reportTitle,
        String securitiesFirm,
        String pdfUrl,
        String detailUrl,
        LocalDate reportDate,
        Integer viewCount
) {
}
