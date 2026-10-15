package com.example.jusikreport.report.service;

import com.example.jusikreport.marketcap.repository.MarketCapRepository;
import com.example.jusikreport.report.crawler.NaverResearchCrawler;
import com.example.jusikreport.report.domain.Report;
import com.example.jusikreport.report.dto.CrawlResult;
import com.example.jusikreport.report.dto.CrawledReport;
import com.example.jusikreport.report.repository.ReportRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReportCrawlServiceImplTest {

    @Mock
    private NaverResearchCrawler naverResearchCrawler;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private MarketCapRepository marketCapRepository;

    @InjectMocks
    private ReportCrawlServiceImpl reportCrawlService;

    @Test
    @DisplayName("targetDate로 리포트를 크롤링하여 중복이 아닌 건만 저장한다")
    void crawlByDateSavesNewReports() {
        LocalDate targetDate = LocalDate.of(2026, 10, 15);
        CrawledReport report1 = new CrawledReport(
                "293490", "카카오게임즈", "제목1", "한화투자증권",
                "https://stock.pstatic.net/test1.pdf", "https://stock.naver.com/research/company/1",
                targetDate, 100
        );
        CrawledReport report2 = new CrawledReport(
                "051500", "CJ프레시웨이", "제목2", "DS투자증권",
                "https://stock.pstatic.net/test2.pdf", "https://stock.naver.com/research/company/2",
                targetDate, 200
        );

        given(naverResearchCrawler.crawlPage(targetDate, 1))
                .willReturn(List.of(report1, report2));
        given(naverResearchCrawler.crawlPage(targetDate, 2))
                .willReturn(List.of());

        // report1: 신규, report2: 이미 존재
        given(reportRepository.existsByStockCodeAndReportTitleAndSecuritiesFirmAndReportDateAndPdfUrl(
                report1.stockCode(), report1.reportTitle(), report1.securitiesFirm(), report1.reportDate(), report1.pdfUrl()
        )).willReturn(false);

        given(reportRepository.existsByStockCodeAndReportTitleAndSecuritiesFirmAndReportDateAndPdfUrl(
                report2.stockCode(), report2.reportTitle(), report2.securitiesFirm(), report2.reportDate(), report2.pdfUrl()
        )).willReturn(true);

        CrawlResult result = reportCrawlService.crawlByDate(targetDate);

        assertThat(result.savedCount()).isEqualTo(1);
        assertThat(result.skippedCount()).isEqualTo(1);
        assertThat(result.failedCount()).isEqualTo(0);
        assertThat(result.visitedPages()).isEqualTo(1);

        verify(reportRepository, times(1)).save(any(Report.class));
    }
}
