package com.example.jusikreport.industry.service;

import com.example.jusikreport.industry.crawler.NaverIndustryResearchCrawler;
import com.example.jusikreport.industry.domain.IndustryReport;
import com.example.jusikreport.industry.dto.CrawledIndustryReport;
import com.example.jusikreport.industry.repository.IndustryReportRepository;
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
class IndustryReportCrawlServiceImplTest {

    @Mock
    private NaverIndustryResearchCrawler crawler;

    @Mock
    private IndustryReportRepository repository;

    @InjectMocks
    private IndustryReportCrawlServiceImpl industryReportCrawlService;

    @Test
    @DisplayName("targetDate로 산업 리포트를 크롤링하여 신규 건만 저장한다")
    void crawlByDateSavesNewIndustryReports() {
        LocalDate targetDate = LocalDate.of(2026, 10, 15);
        CrawledIndustryReport report1 = new CrawledIndustryReport(
                "유틸리티", "제목1", "하나증권",
                "https://stock.pstatic.net/ind1.pdf", "https://stock.naver.com/research/industry/1",
                targetDate, 100
        );
        CrawledIndustryReport report2 = new CrawledIndustryReport(
                "기타", "제목2", "한화투자증권",
                "https://stock.pstatic.net/ind2.pdf", "https://stock.naver.com/research/industry/2",
                targetDate, 200
        );

        given(crawler.crawlPage(targetDate, 1))
                .willReturn(List.of(report1, report2));
        given(crawler.crawlPage(targetDate, 2))
                .willReturn(List.of());

        // report1: 신규, report2: 이미 존재
        given(repository.existsByCategoryNameAndReportTitleAndSecuritiesFirmAndReportDateAndPdfUrl(
                report1.categoryName(), report1.reportTitle(), report1.securitiesFirm(), report1.reportDate(), report1.pdfUrl()
        )).willReturn(false);

        given(repository.existsByCategoryNameAndReportTitleAndSecuritiesFirmAndReportDateAndPdfUrl(
                report2.categoryName(), report2.reportTitle(), report2.securitiesFirm(), report2.reportDate(), report2.pdfUrl()
        )).willReturn(true);

        CrawlResult result = industryReportCrawlService.crawlByDate(targetDate);

        assertThat(result.savedCount()).isEqualTo(1);
        assertThat(result.skippedCount()).isEqualTo(1);
        assertThat(result.failedCount()).isEqualTo(0);
        assertThat(result.visitedPages()).isEqualTo(1);

        verify(repository, times(1)).save(any(IndustryReport.class));
    }
}
