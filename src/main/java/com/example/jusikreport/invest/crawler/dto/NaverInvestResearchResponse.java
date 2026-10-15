package com.example.jusikreport.invest.crawler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NaverInvestResearchResponse(
        Boolean hasNext,
        String totalCount,
        List<NaverInvestResearchItem> items,
        String size,
        String index
) {
    public List<NaverInvestResearchItem> safeItems() {
        return items == null ? List.of() : items;
    }
}
