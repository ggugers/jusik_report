package com.example.jusikreport.debenture.crawler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NaverDebentureResearchResponse(
        Boolean hasNext,
        String totalCount,
        List<NaverDebentureResearchItem> items,
        String size,
        String index
) {
    public List<NaverDebentureResearchItem> safeItems() {
        return items == null ? List.of() : items;
    }
}
