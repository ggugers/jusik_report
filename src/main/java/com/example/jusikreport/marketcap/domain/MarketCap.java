package com.example.jusikreport.marketcap.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "market_caps",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_market_cap_stock_code", columnNames = {"stock_code"}),
                @UniqueConstraint(name = "uk_market_cap_stock_name", columnNames = {"stock_name"})
        }
)
public class MarketCap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stock_code", length = 20)
    private String stockCode;

    @Column(name = "stock_name", nullable = false, length = 100)
    private String stockName;

    @Column(name = "market_type", length = 20)
    private String marketType;

    @Column(name = "market_cap_text", nullable = false, length = 100)
    private String marketCapText;

    @Column(name = "market_cap_value", nullable = false)
    private Long marketCapValue;

    @Column(name = "rank_no")
    private Integer rankNo;

    @Column(name = "current_price")
    private Long currentPrice;

    @Column(name = "current_price_text", length = 50)
    private String currentPriceText;

    @Column(name = "price_change")
    private Long priceChange;

    @Column(name = "price_change_text", length = 50)
    private String priceChangeText;

    @Column(name = "change_rate")
    private Double changeRate;

    @Column(name = "change_rate_text", length = 50)
    private String changeRateText;

    @Column(name = "face_value")
    private Long faceValue;

    @Column(name = "face_value_text", length = 50)
    private String faceValueText;

    @Column(name = "listed_shares")
    private Long listedShares;

    @Column(name = "listed_shares_text", length = 50)
    private String listedSharesText;

    @Column(name = "foreign_ratio")
    private Double foreignRatio;

    @Column(name = "foreign_ratio_text", length = 50)
    private String foreignRatioText;

    @Column(name = "volume")
    private Long volume;

    @Column(name = "volume_text", length = 50)
    private String volumeText;

    @Column(name = "per")
    private Double per;

    @Column(name = "per_text", length = 50)
    private String perText;

    @Column(name = "roe")
    private Double roe;

    @Column(name = "roe_text", length = 50)
    private String roeText;

    @Column(name = "collected_at", nullable = false)
    private LocalDateTime collectedAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected MarketCap() {
    }

    public MarketCap(
            String stockCode,
            String stockName,
            String marketType,
            String marketCapText,
            Long marketCapValue,
            Integer rankNo
    ) {
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.marketType = marketType;
        this.marketCapText = marketCapText;
        this.marketCapValue = marketCapValue;
        this.rankNo = rankNo;
    }

    public MarketCap(
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
            String roeText
    ) {
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.marketType = marketType;
        this.marketCapText = marketCapText;
        this.marketCapValue = marketCapValue;
        this.rankNo = rankNo;
        this.currentPrice = currentPrice;
        this.currentPriceText = currentPriceText;
        this.priceChange = priceChange;
        this.priceChangeText = priceChangeText;
        this.changeRate = changeRate;
        this.changeRateText = changeRateText;
        this.faceValue = faceValue;
        this.faceValueText = faceValueText;
        this.listedShares = listedShares;
        this.listedSharesText = listedSharesText;
        this.foreignRatio = foreignRatio;
        this.foreignRatioText = foreignRatioText;
        this.volume = volume;
        this.volumeText = volumeText;
        this.per = per;
        this.perText = perText;
        this.roe = roe;
        this.roeText = roeText;
    }

    public void update(String stockCode, String stockName, String marketType, String marketCapText, Long marketCapValue, Integer rankNo) {
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.marketType = marketType;
        this.marketCapText = marketCapText;
        this.marketCapValue = marketCapValue;
        this.rankNo = rankNo;
        this.collectedAt = LocalDateTime.now();
    }

    public void update(
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
            String roeText
    ) {
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.marketType = marketType;
        this.marketCapText = marketCapText;
        this.marketCapValue = marketCapValue;
        this.rankNo = rankNo;
        this.currentPrice = currentPrice;
        this.currentPriceText = currentPriceText;
        this.priceChange = priceChange;
        this.priceChangeText = priceChangeText;
        this.changeRate = changeRate;
        this.changeRateText = changeRateText;
        this.faceValue = faceValue;
        this.faceValueText = faceValueText;
        this.listedShares = listedShares;
        this.listedSharesText = listedSharesText;
        this.foreignRatio = foreignRatio;
        this.foreignRatioText = foreignRatioText;
        this.volume = volume;
        this.volumeText = volumeText;
        this.per = per;
        this.perText = perText;
        this.roe = roe;
        this.roeText = roeText;
        this.collectedAt = LocalDateTime.now();
    }

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.collectedAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getStockCode() {
        return stockCode;
    }

    public String getStockName() {
        return stockName;
    }

    public String getMarketType() {
        return marketType;
    }

    public String getMarketCapText() {
        return marketCapText;
    }

    public Long getMarketCapValue() {
        return marketCapValue;
    }

    public Integer getRankNo() {
        return rankNo;
    }

    public Long getCurrentPrice() {
        return currentPrice;
    }

    public String getCurrentPriceText() {
        return currentPriceText;
    }

    public Long getPriceChange() {
        return priceChange;
    }

    public String getPriceChangeText() {
        return priceChangeText;
    }

    public Double getChangeRate() {
        return changeRate;
    }

    public String getChangeRateText() {
        return changeRateText;
    }

    public Long getFaceValue() {
        return faceValue;
    }

    public String getFaceValueText() {
        return faceValueText;
    }

    public Long getListedShares() {
        return listedShares;
    }

    public String getListedSharesText() {
        return listedSharesText;
    }

    public Double getForeignRatio() {
        return foreignRatio;
    }

    public String getForeignRatioText() {
        return foreignRatioText;
    }

    public Long getVolume() {
        return volume;
    }

    public String getVolumeText() {
        return volumeText;
    }

    public Double getPer() {
        return per;
    }

    public String getPerText() {
        return perText;
    }

    public Double getRoe() {
        return roe;
    }

    public String getRoeText() {
        return roeText;
    }

    public LocalDateTime getCollectedAt() {
        return collectedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
