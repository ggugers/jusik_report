package com.example.jusikreport.invest;

import com.example.jusikreport.invest.domain.InvestReport;
import com.example.jusikreport.invest.repository.InvestReportRepository;
import com.example.jusikreport.invest.service.InvestReportCrawlService;
import com.example.jusikreport.report.dto.CrawlResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class InvestReportCrawlIntegrationTest {

    @Autowired
    private InvestReportCrawlService investReportCrawlService;

    @Autowired
    private InvestReportRepository investReportRepository;

    @Test
    @DisplayName("리뉴얼된 API로 2026-10-15 일자 투자정보 리포트를 크롤링하여 DB에 정상 저장한다")
    void testCrawlByDateIntegration() {
        LocalDate targetDate = LocalDate.of(2026, 10, 15);

        CrawlResult result = investReportCrawlService.crawlByDate(targetDate);

        assertThat(result.failedCount()).isZero();
        assertThat(result.savedCount() + result.skippedCount()).isGreaterThan(0);

        List<InvestReport> reports = investReportRepository.findAll();
        assertThat(reports).anyMatch(r -> r.getReportDate().equals(targetDate)
                && r.getDetailUrl().startsWith("https://stock.naver.com/research/invest/"));
    }
}
