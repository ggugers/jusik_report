package com.example.jusikreport.invest.crawler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NaverInvestResearchDetail(
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
