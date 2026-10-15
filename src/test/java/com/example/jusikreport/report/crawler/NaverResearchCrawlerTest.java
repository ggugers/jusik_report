package com.example.jusikreport.report.crawler;

import com.example.jusikreport.report.dto.CrawledReport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NaverResearchCrawlerTest {

    private final NaverResearchCrawler crawler = new NaverResearchCrawler();

    @Test
    @DisplayName("리뉴얼된 네이버 증권 API를 통해 지정 일자 리포트를 정상 수집한다")
    void crawlPageByTargetDate() {
        LocalDate targetDate = LocalDate.of(2026, 10, 15);

        List<CrawledReport> reports = crawler.crawlPage(targetDate, 1);

        assertThat(reports).isNotEmpty();
        CrawledReport firstReport = reports.get(0);
        assertThat(firstReport.stockCode()).isNotBlank();
        assertThat(firstReport.stockName()).isNotBlank();
        assertThat(firstReport.reportTitle()).isNotBlank();
        assertThat(firstReport.securitiesFirm()).isNotBlank();
        assertThat(firstReport.reportDate()).isEqualTo(targetDate);
        assertThat(firstReport.detailUrl()).startsWith("https://stock.naver.com/research/company/");
        assertThat(firstReport.pdfUrl()).contains(".pdf");
    }

    @Test
    @DisplayName("리뉴얼된 네이버 증권 웹 URL을 파싱하여 리포트를 정상 수집한다")
    void crawlByWebUrl() {
        String webUrl = "https://stock.naver.com/research/company?startDate=20261015&endDate=20261015";

        List<CrawledReport> reports = crawler.crawlByWebUrl(webUrl, 1);

        assertThat(reports).isNotEmpty();
        assertThat(reports).allMatch(report -> report.reportDate().equals(LocalDate.of(2026, 10, 15)));
    }

    @Test
    @DisplayName("특정 종목코드 리포트를 정상 수집한다")
    void crawlPageByItemCode() {
        // 삼성전자 (005930)
        List<CrawledReport> reports = crawler.crawlPageByItemCode("005930", 1);

        assertThat(reports).isNotEmpty();
        assertThat(reports.get(0).stockCode()).isEqualTo("005930");
        assertThat(reports.get(0).stockName()).isEqualTo("삼성전자");
    }
}
