package com.example.jusikreport.debenture.service;

import com.example.jusikreport.debenture.crawler.NaverDebentureResearchCrawler;
import com.example.jusikreport.debenture.domain.DebentureReport;
import com.example.jusikreport.debenture.dto.CrawledDebentureReport;
import com.example.jusikreport.debenture.repository.DebentureReportRepository;
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
class DebentureReportCrawlServiceImplTest {

    @Mock
    private NaverDebentureResearchCrawler crawler;

    @Mock
    private DebentureReportRepository repository;

    @InjectMocks
    private DebentureReportCrawlServiceImpl debentureReportCrawlService;

    @Test
    @DisplayName("targetDate로 채권분석 리포트를 크롤링하여 신규 건만 저장한다")
    void crawlByDateSavesNewDebentureReports() {
        LocalDate targetDate = LocalDate.of(2026, 10, 15);
        CrawledDebentureReport report1 = new CrawledDebentureReport(
                "제목1", "하나증권",
                "https://stock.pstatic.net/deb1.pdf", "https://stock.naver.com/research/debenture/1",
                targetDate, 100
        );
        CrawledDebentureReport report2 = new CrawledDebentureReport(
                "제목2", "한화투자증권",
                "https://stock.pstatic.net/deb2.pdf", "https://stock.naver.com/research/debenture/2",
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

        CrawlResult result = debentureReportCrawlService.crawlByDate(targetDate);

        assertThat(result.savedCount()).isEqualTo(1);
        assertThat(result.skippedCount()).isEqualTo(1);
        assertThat(result.failedCount()).isEqualTo(0);
        assertThat(result.visitedPages()).isEqualTo(1);

        verify(repository, times(1)).save(any(DebentureReport.class));
    }
}
