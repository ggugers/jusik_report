package com.example.jusikreport.economy.crawler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NaverEconomyResearchResponse(
        Boolean hasNext,
        String totalCount,
        List<NaverEconomyResearchItem> items,
        String size,
        String index
) {
    public List<NaverEconomyResearchItem> safeItems() {
        return items == null ? List.of() : items;
    }
}
