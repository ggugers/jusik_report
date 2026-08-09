package com.example.jusikreport.debenture.service;

import com.example.jusikreport.debenture.crawler.NaverDebentureResearchCrawler;
import com.example.jusikreport.debenture.domain.DebentureReport;
import com.example.jusikreport.debenture.dto.CrawledDebentureReport;
import com.example.jusikreport.debenture.repository.DebentureReportRepository;
import com.example.jusikreport.report.dto.CrawlResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class DebentureReportCrawlServiceImpl implements DebentureReportCrawlService {

    private static final int MAX_PAGES = 1500;

    private final NaverDebentureResearchCrawler crawler;
    private final DebentureReportRepository repository;

    public DebentureReportCrawlServiceImpl(NaverDebentureResearchCrawler crawler, DebentureReportRepository repository) {
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
            List<CrawledDebentureReport> crawled = crawler.crawlPage(page);
            if (crawled.isEmpty()) {
                break;
            }

            visitedPages++;
            boolean reachedOlderDate = false;

            for (CrawledDebentureReport item : crawled) {
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
                    repository.save(new DebentureReport(
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
