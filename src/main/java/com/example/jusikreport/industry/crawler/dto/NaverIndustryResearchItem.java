package com.example.jusikreport.industry.crawler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NaverIndustryResearchItem(
        String nid,
        String title,
        String content,
        String brokerName,
        String brokerCode,
        String writeDate,
        String analystName,
        String readCount,
        String industry,
        String industryKoreanName
) {
    public String resolvedCategoryName() {
        if (industryKoreanName != null && !industryKoreanName.isBlank()) {
            return industryKoreanName.trim();
        }
        if (industry != null && !industry.isBlank()) {
            return industry.trim();
        }
        return "기타";
    }
}
