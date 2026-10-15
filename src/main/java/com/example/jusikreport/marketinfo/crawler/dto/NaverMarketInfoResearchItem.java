package com.example.jusikreport.marketinfo.crawler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NaverMarketInfoResearchItem(
        String nid,
        String title,
        String content,
        String brokerName,
        String brokerCode,
        String writeDate,
        String readCount
) {
}
