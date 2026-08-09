package com.example.jusikreport.marketinfo.dto;

import java.time.LocalDate;

public record CrawledMarketInfoReport(
        String reportTitle,
        String securitiesFirm,
        String pdfUrl,
        String detailUrl,
        LocalDate reportDate,
        Integer viewCount
) {
}
