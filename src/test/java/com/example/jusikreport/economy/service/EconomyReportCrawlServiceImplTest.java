package com.example.jusikreport.economy.service;

import com.example.jusikreport.economy.crawler.NaverEconomyResearchCrawler;
import com.example.jusikreport.economy.domain.EconomyReport;
import com.example.jusikreport.economy.dto.CrawledEconomyReport;
import com.example.jusikreport.economy.repository.EconomyReportRepository;
import com.example.jusikreport.report.dto.CrawlResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EconomyReportCrawlServiceImplTest {

    @Mock
    private NaverEconomyResearchCrawler crawler;

    @Mock
    private EconomyReportRepository repository;

    @InjectMocks
    private EconomyReportCrawlServiceImpl economyReportCrawlService;

    @Test
    @DisplayName("targetDate로 경제분석 리포트를 크롤링하여 신규 건만 저장한다")
    void crawlByDateSavesNewEconomyReports() {
        LocalDate targetDate = LocalDate.of(2026, 10, 15);
        CrawledEconomyReport report1 = new CrawledEconomyReport(
                "제목1", "하나증권",
                "https://stock.pstatic.net/eco1.pdf", "https://stock.naver.com/research/economy/1",
                targetDate, 100
        );
        CrawledEconomyReport report2 = new CrawledEconomyReport(
                "제목2", "한화투자증권",
                "https://stock.pstatic.net/eco2.pdf", "https://stock.naver.com/research/economy/2",
                targetDate, 200
        );

        given(crawler.crawlPage(targetDate, 1))
                .willReturn(List.of(report1, report2));
        given(crawler.crawlPage(targetDate, 2))
                .willReturn(List.of());

        // report1: 신규, report2: 이미 존재
        given(repository.existsByReportTitleAndSecuritiesFirmAndReportDateAndPdfUrl(
                report1.reportTitle(), report1.securitiesFirm(), report1.reportDate(), report1.pdfUrl()
        )).willReturn(false);

        given(repository.existsByReportTitleAndSecuritiesFirmAndReportDateAndPdfUrl(
                report2.reportTitle(), report2.securitiesFirm(), report2.reportDate(), report2.pdfUrl()
        )).willReturn(true);

        CrawlResult result = economyReportCrawlService.crawlByDate(targetDate);

        assertThat(result.savedCount()).isEqualTo(1);
        assertThat(result.skippedCount()).isEqualTo(1);
        assertThat(result.failedCount()).isEqualTo(0);
        assertThat(result.visitedPages()).isEqualTo(1);

        verify(repository, times(1)).save(any(EconomyReport.class));
    }
}
