package com.example.jusikreport.economy.service;

import com.example.jusikreport.economy.crawler.NaverEconomyResearchCrawler;
import com.example.jusikreport.economy.domain.EconomyReport;
import com.example.jusikreport.economy.dto.CrawledEconomyReport;
import com.example.jusikreport.economy.repository.EconomyReportRepository;
import com.example.jusikreport.report.dto.CrawlResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class EconomyReportCrawlServiceImpl implements EconomyReportCrawlService {

    private static final int MAX_PAGES = 1500;

    private final NaverEconomyResearchCrawler crawler;
    private final EconomyReportRepository repository;

    public EconomyReportCrawlServiceImpl(NaverEconomyResearchCrawler crawler, EconomyReportRepository repository) {
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
            List<CrawledEconomyReport> crawled = crawler.crawlPage(page);
            if (crawled.isEmpty()) {
                break;
            }

            visitedPages++;
            boolean reachedOlderDate = false;

            for (CrawledEconomyReport item : crawled) {
                try {
                    if (item.reportDate().isAfter(targetDate)) {
                        continue;
                    }
                    if (item.reportDate().isBefore(targetDate)) {
                        reachedOlderDate = true;
                        break;
                    }
                    if (repository.existsByReportTitleAndSecuritiesFirmAndReportDateAndPdfUrl(
                            item.reportTitle(),
                            item.securitiesFirm(),
                            item.reportDate(),
                            item.pdfUrl()
                    )) {
                        skippedCount++;
                        continue;
                    }
                    repository.save(new EconomyReport(
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
