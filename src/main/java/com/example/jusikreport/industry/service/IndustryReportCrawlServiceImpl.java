package com.example.jusikreport.industry.service;

import com.example.jusikreport.industry.crawler.NaverIndustryResearchCrawler;
import com.example.jusikreport.industry.domain.IndustryReport;
import com.example.jusikreport.industry.dto.CrawledIndustryReport;
import com.example.jusikreport.industry.repository.IndustryReportRepository;
import com.example.jusikreport.report.dto.CrawlResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class IndustryReportCrawlServiceImpl implements IndustryReportCrawlService {

    private static final int MAX_PAGES = 1500;

    private final NaverIndustryResearchCrawler crawler;
    private final IndustryReportRepository repository;

    public IndustryReportCrawlServiceImpl(NaverIndustryResearchCrawler crawler, IndustryReportRepository repository) {
        this.crawler = crawler;
        this.repository = repository;
    }

    @Override
    public CrawlResult crawlByDate(LocalDate targetDate) {
        int savedCount = 0;
        int skippedCount = 0;
        int failedCount = 0;
        int visitedPages = 0;

        for (int page = 1; page <= MAX_PAGES; page++) {
            List<CrawledIndustryReport> crawled = crawler.crawlPage(targetDate, page);
            if (crawled.isEmpty()) {
                break;
            }

            visitedPages++;
            boolean reachedOlderDate = false;

            for (CrawledIndustryReport item : crawled) {
                try {
                    if (item.reportDate().isAfter(targetDate)) {
                        continue;
                    }
                    if (item.reportDate().isBefore(targetDate)) {
                        reachedOlderDate = true;
                        break;
                    }
                    if (repository.existsByCategoryNameAndReportTitleAndSecuritiesFirmAndReportDateAndPdfUrl(
                            item.categoryName(),
                            item.reportTitle(),
                            item.securitiesFirm(),
                            item.reportDate(),
                            item.pdfUrl()
                    )) {
                        skippedCount++;
                        continue;
                    }
                    repository.save(new IndustryReport(
                            item.categoryName(),
                            item.reportTitle(),
                            item.securitiesFirm(),
                            item.pdfUrl(),
                            item.detailUrl(),
                            item.reportDate(),
                            item.viewCount(),
                            page
                    ));
                    savedCount++;
                } catch (Exception exception) {
                    failedCount++;
                }
            }

            if (reachedOlderDate) {
                break;
            }
        }

        return new CrawlResult(targetDate, savedCount, skippedCount, failedCount, visitedPages);
    }
}
