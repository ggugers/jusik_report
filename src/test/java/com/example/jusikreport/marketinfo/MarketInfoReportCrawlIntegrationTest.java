package com.example.jusikreport.marketinfo;

import com.example.jusikreport.marketinfo.domain.MarketInfoReport;
import com.example.jusikreport.marketinfo.repository.MarketInfoReportRepository;
import com.example.jusikreport.marketinfo.service.MarketInfoReportCrawlService;
import com.example.jusikreport.report.dto.CrawlResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MarketInfoReportCrawlIntegrationTest {

    @Autowired
    private MarketInfoReportCrawlService marketInfoReportCrawlService;

    @Autowired
    private MarketInfoReportRepository marketInfoReportRepository;

    @Test
    @DisplayName("리뉴얼된 API로 2026-10-15 일자 시황정보 리포트를 크롤링하여 DB에 정상 저장한다")
    void testCrawlByDateIntegration() {
        LocalDate targetDate = LocalDate.of(2026, 10, 15);

        CrawlResult result = marketInfoReportCrawlService.crawlByDate(targetDate);

        assertThat(result.failedCount()).isZero();
        assertThat(result.savedCount() + result.skippedCount()).isGreaterThan(0);

        List<MarketInfoReport> reports = marketInfoReportRepository.findAll();
        assertThat(reports).anyMatch(r -> r.getReportDate().equals(targetDate)
                && r.getDetailUrl().startsWith("https://stock.naver.com/research/daily/"));
    }
}
