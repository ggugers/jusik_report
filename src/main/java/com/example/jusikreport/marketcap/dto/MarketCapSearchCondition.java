package com.example.jusikreport.marketcap.dto;

public record MarketCapSearchCondition(
        String stockName,
        String marketType,
        Long minMarketCap,
        Long maxMarketCap
) {
    public MarketCapSearchCondition(String stockName, String marketType) {
        this(stockName, marketType, null, null);
    }
}
