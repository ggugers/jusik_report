package com.example.jusikreport.invest.crawler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NaverInvestResearchItem(
        String nid,
        String title,
        String content,
        String brokerName,
        String brokerCode,
        String writeDate,
        String readCount
) {
}
