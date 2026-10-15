package com.example.jusikreport.economy.crawler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NaverEconomyResearchDetail(
        String nid,
        String brokerName,
        String brokerCode,
        String title,
        String content,
        String readCount,
        String writeDate,
        String attachUrl,
        String attachName
) {
}
