package com.example.jusikreport.marketcap.crawler;

import org.springframework.stereotype.Component;

@Component
public class MarketCapValueParser {

    public long parse(String marketCapText) {
        String digits = marketCapText.replace(",", "").trim();
        if (digits.isEmpty()) {
            throw new IllegalArgumentException("시가총액 값이 비어 있습니다.");
        }
        return Long.parseLong(digits);
    }
}
