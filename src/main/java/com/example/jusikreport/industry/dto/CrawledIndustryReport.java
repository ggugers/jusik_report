package com.example.jusikreport.industry.dto;

import java.time.LocalDate;

public record CrawledIndustryReport(
        String categoryName,
        String reportTitle,
        String securitiesFirm,
        String pdfUrl,
        String detailUrl,
        LocalDate reportDate,
        Integer viewCount
) {
}
