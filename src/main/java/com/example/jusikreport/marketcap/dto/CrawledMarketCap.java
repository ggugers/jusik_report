package com.example.jusikreport.marketcap.dto;

public record CrawledMarketCap(
        String stockCode,
        String stockName,
        String marketType,
        String marketCapText,
        Long marketCapValue,
        Integer rankNo,
        Long currentPrice,
        String currentPriceText,
        Long priceChange,
        String priceChangeText,
        Double changeRate,
        String changeRateText,
        Long faceValue,
        String faceValueText,
        Long listedShares,
        String listedSharesText,
        Double foreignRatio,
        String foreignRatioText,
        Long volume,
        String volumeText,
        Double per,
        String perText,
        Double roe,
        String roeText,
        Double pbr,
        String pbrText,
        Double operatingProfitGrowthRate,
        String operatingProfitGrowthRateText
) {
}
