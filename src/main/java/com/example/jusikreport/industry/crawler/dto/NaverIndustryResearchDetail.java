package com.example.jusikreport.industry.crawler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NaverIndustryResearchDetail(
        String nid,
        String brokerName,
        String brokerCode,
        String title,
        String content,
        String readCount,
        String writeDate,
        String attachUrl,
        String attachName,
        String industry,
        String industryKoreanName
) {
}
