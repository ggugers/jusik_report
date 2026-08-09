package com.example.jusikreport.marketcap.dto;

public record SyncResult(
        int savedCount,
        int updatedCount,
        int failedCount,
        int visitedPages
) {
}
