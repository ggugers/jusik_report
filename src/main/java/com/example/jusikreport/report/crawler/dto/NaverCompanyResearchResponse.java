package com.example.jusikreport.report.crawler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NaverCompanyResearchResponse(
        Boolean hasNext,
        String totalCount,
        List<NaverCompanyResearchItem> items,
        String size,
        String index
) {
    public List<NaverCompanyResearchItem> safeItems() {
        return items == null ? List.of() : items;
    }
}
