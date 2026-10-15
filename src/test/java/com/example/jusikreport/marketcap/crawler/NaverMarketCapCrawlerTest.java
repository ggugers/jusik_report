package com.example.jusikreport.marketcap.crawler;

import com.example.jusikreport.marketcap.dto.CrawledMarketCap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NaverMarketCapCrawlerTest {

    @Test
    @DisplayName("네이버 시가총액 HTML 테이블 파싱 테스트 (영업이익증가율 포함 14개 컬럼 수집)")
    void testParseHtml() {
        MarketCapValueParser parser = new MarketCapValueParser();
        NaverMarketCapCrawler crawler = new NaverMarketCapCrawler(parser);

        List<CrawledMarketCap> items = crawler.crawlPage(1, null, "KOSPI");
        assertThat(items).isNotEmpty();

        CrawledMarketCap firstItem = items.get(0);
        assertThat(firstItem.stockCode()).isNotBlank();
        assertThat(firstItem.stockName()).isNotBlank();
        assertThat(firstItem.currentPrice()).isNotNull();
        assertThat(firstItem.marketCapValue()).isNotNull();
    }
}
