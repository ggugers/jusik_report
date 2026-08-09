package com.example.jusikreport.report.service;

import com.example.jusikreport.report.crawler.NaverResearchCrawler;
import com.example.jusikreport.marketcap.domain.MarketCap;
import com.example.jusikreport.marketcap.repository.MarketCapRepository;
import com.example.jusikreport.report.domain.Report;
import com.example.jusikreport.report.dto.CrawlResult;
import com.example.jusikreport.report.dto.CrawledReport;
import com.example.jusikreport.report.repository.ReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ReportCrawlServiceImpl implements ReportCrawlService {

    private static final Logger log = LoggerFactory.getLogger(ReportCrawlServiceImpl.class);

    private static final int MAX_PAGES = 3000;

    private final NaverResearchCrawler naverResearchCrawler;
    private final ReportRepository reportRepository;
    private final MarketCapRepository marketCapRepository;

    public ReportCrawlServiceImpl(
            NaverResearchCrawler naverResearchCrawler,
            ReportRepository reportRepository,
            MarketCapRepository marketCapRepository
    ) {
        this.naverResearchCrawler = naverResearchCrawler;
        this.reportRepository = reportRepository;
        this.marketCapRepository = marketCapRepository;
    }

    @Override
    public CrawlResult crawlByDate(LocalDate targetDate) {
        return crawl(targetDate, null);
    }

    @Override
    public CrawlResult crawlByStockName(String stockName, LocalDate targetDate) {
        String keyword = stockName == null ? "" : stockName.trim();
        if (keyword.isEmpty()) {
            throw new IllegalArgumentException("stockName is required.");
        }

        String itemCode = keyword.matches("\\d{6}")
                ? keyword
                : marketCapRepository.findByStockName(keyword)
                .map(MarketCap::getStockCode)
                .orElseThrow(() -> new IllegalArgumentException("stock code not found. stockName=" + keyword));

        log.info("Crawl stock report by stockName={}, itemCode={}, targetDate={}", keyword, itemCode, targetDate);

        return crawl(targetDate, itemCode);
    }

    private CrawlResult crawl(LocalDate targetDate, String itemCode) {
        int savedCount = 0;
        int skippedCount = 0;
        int failedCount = 0;
        int visitedPages = 0;
        Set<String> visitedPageKeys = new HashSet<>();

        for (int page = 1; page <= MAX_PAGES; page++) {
            List<CrawledReport> crawledReports = itemCode == null
                    ? naverResearchCrawler.crawlPage(page)
                    : naverResearchCrawler.crawlPageByItemCode(itemCode, page);
            if (crawledReports.isEmpty()) {
                break;
            }

            String pageKey = buildPageKey(crawledReports);
            if (!visitedPageKeys.add(pageKey)) {
                log.info("Stop stock report crawl because repeated page was detected. itemCode={}, page={}", itemCode, page);
                break;
            }

            visitedPages++;
            boolean reachedOlderDate = false;

            for (CrawledReport item : crawledReports) {
                try {
                    if (targetDate != null && item.reportDate().isAfter(targetDate)) {
                        continue;
                    }

                    if (targetDate != null && item.reportDate().isBefore(targetDate)) {
                        reachedOlderDate = true;
                        break;
                    }

                    if (reportRepository.existsByStockCodeAndReportTitleAndSecuritiesFirmAndReportDateAndPdfUrl(
                            item.stockCode(),
                            item.reportTitle(),
                            item.securitiesFirm(),
                            item.reportDate(),
                            item.pdfUrl()
                    )) {
                        skippedCount++;
                        continue;
                    }

                    reportRepository.save(new Report(
                            item.stockCode(),
                            item.stockName(),
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

    private String buildPageKey(List<CrawledReport> crawledReports) {
        StringBuilder builder = new StringBuilder();
        for (CrawledReport report : crawledReports) {
            builder.append(report.stockCode()).append('|')
                    .append(report.reportTitle()).append('|')
                    .append(report.securitiesFirm()).append('|')
                    .append(report.reportDate()).append('|')
                    .append(report.pdfUrl()).append('\n');
        }
        return builder.toString();
    }
}
