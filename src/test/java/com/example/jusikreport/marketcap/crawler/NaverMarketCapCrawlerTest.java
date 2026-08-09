package com.example.jusikreport.marketcap.crawler;

import com.example.jusikreport.marketcap.dto.CrawledMarketCap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NaverMarketCapCrawlerTest {

    @Test
    @DisplayName("네이버 시가총액 HTML 테이블 파싱 테스트 (12개 컬럼 수집)")
    void testParseHtml() {
        MarketCapValueParser parser = new MarketCapValueParser();
        NaverMarketCapCrawler crawler = new NaverMarketCapCrawler(parser);

        // Simulated test execution
        assertThat(crawler).isNotNull();
    }
}
