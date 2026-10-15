package com.example.jusikreport.marketinfo.crawler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NaverMarketInfoResearchResponse(
        Boolean hasNext,
        String totalCount,
        List<NaverMarketInfoResearchItem> items,
        String size,
        String index
) {
    public List<NaverMarketInfoResearchItem> safeItems() {
        return items == null ? List.of() : items;
    }
}
