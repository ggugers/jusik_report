package com.example.jusikreport.debenture.dto;

import java.time.LocalDate;

public record CrawledDebentureReport(
        String reportTitle,
        String securitiesFirm,
        String pdfUrl,
        String detailUrl,
        LocalDate reportDate,
        Integer viewCount
) {
}
