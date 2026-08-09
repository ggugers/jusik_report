package com.example.jusikreport.report.dto;

import java.time.LocalDate;

public record CrawlResult(
        LocalDate targetDate,
        int savedCount,
        int skippedCount,
        int failedCount,
        int visitedPages
) {
}
