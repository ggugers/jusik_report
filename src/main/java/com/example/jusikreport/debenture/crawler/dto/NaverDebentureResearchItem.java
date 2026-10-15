package com.example.jusikreport.debenture.crawler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NaverDebentureResearchItem(
        String nid,
        String title,
        String content,
        String brokerName,
        String brokerCode,
        String writeDate,
        String readCount
) {
}
