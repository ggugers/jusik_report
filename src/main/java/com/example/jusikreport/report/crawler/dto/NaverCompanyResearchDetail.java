package com.example.jusikreport.report.crawler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NaverCompanyResearchDetail(
        String nid,
        String brokerName,
        String brokerCode,
        String title,
        String content,
        String readCount,
        String writeDate,
        String attachUrl,
        String attachName,
        String itemCode,
        String itemName,
        String opinionText,
        String opinionType,
        String goalPrice,
        String prevGoalPrice,
        String priceAtWriteDate
) {
}
