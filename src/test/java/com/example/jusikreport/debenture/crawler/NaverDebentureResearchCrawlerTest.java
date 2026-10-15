package com.example.jusikreport.debenture.crawler;

import com.example.jusikreport.debenture.dto.CrawledDebentureReport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NaverDebentureResearchCrawlerTest {

    private final NaverDebentureResearchCrawler crawler = new NaverDebentureResearchCrawler();

    @Test
    @DisplayName("리뉴얼된 네이버 증권 채권분석 리서치 API를 통해 지정 일자 리포트를 정상 수집한다")
    void crawlPageByTargetDate() {
        LocalDate targetDate = LocalDate.of(2026, 10, 15);

        List<CrawledDebentureReport> reports = crawler.crawlPage(targetDate, 1);

        assertThat(reports).isNotEmpty();
        CrawledDebentureReport firstReport = reports.get(0);
        assertThat(firstReport.reportTitle()).isNotBlank();
        assertThat(firstReport.securitiesFirm()).isNotBlank();
        assertThat(firstReport.reportDate()).isEqualTo(targetDate);
        assertThat(firstReport.detailUrl()).startsWith("https://stock.naver.com/research/debenture/");
        assertThat(firstReport.pdfUrl()).contains(".pdf");
    }

    @Test
    @DisplayName("리뉴얼된 네이버 증권 채권분석 리서치 웹 URL을 파싱하여 리포트를 정상 수집한다")
    void crawlByWebUrl() {
        String webUrl = "https://stock.naver.com/research/debenture?startDate=20261015&endDate=20261015";

        List<CrawledDebentureReport> reports = crawler.crawlByWebUrl(webUrl, 1);

        assertThat(reports).isNotEmpty();
        assertThat(reports).allMatch(report -> report.reportDate().equals(LocalDate.of(2026, 10, 15)));
    }
}
