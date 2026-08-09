package com.example.jusikreport.invest.dto;

import java.time.LocalDate;

public record CrawledInvestReport(
        String reportTitle,
        String securitiesFirm,
        String pdfUrl,
        String detailUrl,
        LocalDate reportDate,
        Integer viewCount
) {
}
