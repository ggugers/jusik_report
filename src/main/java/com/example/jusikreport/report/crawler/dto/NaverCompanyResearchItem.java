package com.example.jusikreport.report.crawler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NaverCompanyResearchItem(
        String nid,
        String title,
        String content,
        String brokerName,
        String brokerCode,
        String writeDate,
        String readCount,
        String itemCode,
        String itemName,
        String goalPrice,
        String opinionText,
        String opinionType
) {
}
