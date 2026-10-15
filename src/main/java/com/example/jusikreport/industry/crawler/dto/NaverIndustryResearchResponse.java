package com.example.jusikreport.industry.crawler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NaverIndustryResearchResponse(
        Boolean hasNext,
        String totalCount,
        List<NaverIndustryResearchItem> items,
        String size,
        String index
) {
    public List<NaverIndustryResearchItem> safeItems() {
        return items == null ? List.of() : items;
    }
}
