package com.example.jusikreport.report;

import com.example.jusikreport.report.domain.Report;
import com.example.jusikreport.report.dto.CrawlResult;
import com.example.jusikreport.report.repository.ReportRepository;
import com.example.jusikreport.report.service.ReportCrawlService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ReportCrawlIntegrationTest {

    @Autowired
    private ReportCrawlService reportCrawlService;

    @Autowired
    private ReportRepository reportRepository;

    @Test
    @DisplayName("리뉴얼된 API로 2026-10-15 일자 리포트를 크롤링하여 DB에 정상 저장한다")
    void testCrawlByDateIntegration() {
        LocalDate targetDate = LocalDate.of(2026, 10, 15);

        CrawlResult result = reportCrawlService.crawlByDate(targetDate);

        assertThat(result.failedCount()).isZero();
        assertThat(result.savedCount() + result.skippedCount()).isGreaterThan(0);

        List<Report> reports = reportRepository.findAll();
        assertThat(reports).anyMatch(r -> r.getReportDate().equals(targetDate)
                && r.getDetailUrl().startsWith("https://stock.naver.com/research/company/"));
    }
}
