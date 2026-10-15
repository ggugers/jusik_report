package com.example.jusikreport.economy.crawler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NaverEconomyResearchItem(
        String nid,
        String title,
        String content,
        String brokerName,
        String brokerCode,
        String writeDate,
        String readCount
) {
}
