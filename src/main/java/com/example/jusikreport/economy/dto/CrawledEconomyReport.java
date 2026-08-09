package com.example.jusikreport.economy.dto;

import java.time.LocalDate;

public record CrawledEconomyReport(
        String reportTitle,
        String securitiesFirm,
        String pdfUrl,
        String detailUrl,
        LocalDate reportDate,
        Integer viewCount
) {
}
