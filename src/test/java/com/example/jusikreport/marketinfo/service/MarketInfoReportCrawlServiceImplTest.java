package com.example.jusikreport.marketinfo.service;

import com.example.jusikreport.marketinfo.crawler.NaverMarketInfoResearchCrawler;
import com.example.jusikreport.marketinfo.domain.MarketInfoReport;
import com.example.jusikreport.marketinfo.dto.CrawledMarketInfoReport;
import com.example.jusikreport.marketinfo.repository.MarketInfoReportRepository;
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
class MarketInfoReportCrawlServiceImplTest {

    @Mock
    private NaverMarketInfoResearchCrawler crawler;

    @Mock
    private MarketInfoReportRepository repository;

    @InjectMocks
    private MarketInfoReportCrawlServiceImpl marketInfoReportCrawlService;

    @Test
    @DisplayName("targetDate로 시황정보 리포트를 크롤링하여 신규 건만 저장한다")
    void crawlByDateSavesNewMarketInfoReports() {
        LocalDate targetDate = LocalDate.of(2026, 10, 15);
        CrawledMarketInfoReport report1 = new CrawledMarketInfoReport(
                "제목1", "하나증권",
                "https://stock.pstatic.net/mkt1.pdf", "https://stock.naver.com/research/daily/1",
                targetDate, 100
        );
        CrawledMarketInfoReport report2 = new CrawledMarketInfoReport(
                "제목2", "한화투자증권",
                "https://stock.pstatic.net/mkt2.pdf", "https://stock.naver.com/research/daily/2",
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

        CrawlResult result = marketInfoReportCrawlService.crawlByDate(targetDate);

        assertThat(result.savedCount()).isEqualTo(1);
        assertThat(result.skippedCount()).isEqualTo(1);
        assertThat(result.failedCount()).isEqualTo(0);
        assertThat(result.visitedPages()).isEqualTo(1);

        verify(repository, times(1)).save(any(MarketInfoReport.class));
    }
}
