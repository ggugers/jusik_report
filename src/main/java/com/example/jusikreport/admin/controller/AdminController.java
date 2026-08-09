package com.example.jusikreport.admin.controller;

import com.example.jusikreport.debenture.service.DebentureReportCrawlService;
import com.example.jusikreport.industry.service.IndustryReportCrawlService;
import com.example.jusikreport.economy.service.EconomyReportCrawlService;
import com.example.jusikreport.invest.service.InvestReportCrawlService;
import com.example.jusikreport.marketcap.dto.SyncResult;
import com.example.jusikreport.marketcap.service.MarketCapSyncService;
import com.example.jusikreport.marketinfo.service.MarketInfoReportCrawlService;
import com.example.jusikreport.report.dto.CrawlResult;
import com.example.jusikreport.report.service.ReportCrawlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final ReportCrawlService reportCrawlService;
    private final IndustryReportCrawlService industryReportCrawlService;
    private final MarketInfoReportCrawlService marketInfoReportCrawlService;
    private final InvestReportCrawlService investReportCrawlService;
    private final EconomyReportCrawlService economyReportCrawlService;
    private final DebentureReportCrawlService debentureReportCrawlService;
    private final MarketCapSyncService marketCapSyncService;

    public AdminController(
            ReportCrawlService reportCrawlService,
            IndustryReportCrawlService industryReportCrawlService,
            MarketInfoReportCrawlService marketInfoReportCrawlService,
            InvestReportCrawlService investReportCrawlService,
            EconomyReportCrawlService economyReportCrawlService,
            DebentureReportCrawlService debentureReportCrawlService,
            MarketCapSyncService marketCapSyncService
    ) {
        this.reportCrawlService = reportCrawlService;
        this.industryReportCrawlService = industryReportCrawlService;
        this.marketInfoReportCrawlService = marketInfoReportCrawlService;
        this.investReportCrawlService = investReportCrawlService;
        this.economyReportCrawlService = economyReportCrawlService;
        this.debentureReportCrawlService = debentureReportCrawlService;
        this.marketCapSyncService = marketCapSyncService;
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("today", LocalDate.now());
        return "admin/index";
    }

    @PostMapping("/admin/reports/crawl")
    public String crawlReport(
            @RequestParam String reportType,
            @RequestParam(required = false) LocalDate targetDate,
            @RequestParam(required = false) String stockName,
            RedirectAttributes redirectAttributes
    ) {
        CrawlTarget target = CrawlTarget.from(reportType);
        String normalizedStockName = stockName == null ? "" : stockName.trim();

        if (target != CrawlTarget.STOCK && targetDate == null) {
            redirectAttributes.addFlashAttribute("selectedReportType", target.value);
            redirectAttributes.addFlashAttribute("selectedStockName", normalizedStockName);
            redirectAttributes.addFlashAttribute("message", "targetDate is required.");
            return "redirect:/admin";
        }

        if (target == CrawlTarget.STOCK && targetDate == null && normalizedStockName.isEmpty()) {
            redirectAttributes.addFlashAttribute("selectedReportType", target.value);
            redirectAttributes.addFlashAttribute("message", "targetDate or stockName is required.");
            return "redirect:/admin";
        }

        CrawlExecutionResult executionResult;
        try {
            executionResult = switch (target) {
                case ALL -> crawlAllByDate(targetDate);
                case STOCK -> crawlSingle(target, normalizedStockName.isEmpty()
                        ? reportCrawlService.crawlByDate(targetDate)
                        : reportCrawlService.crawlByStockName(normalizedStockName, targetDate));
                case INDUSTRY -> crawlSingle(target, industryReportCrawlService.crawlByDate(targetDate));
                case MARKET_INFO -> crawlSingle(target, marketInfoReportCrawlService.crawlByDate(targetDate));
                case INVEST -> crawlSingle(target, investReportCrawlService.crawlByDate(targetDate));
                case ECONOMY -> crawlSingle(target, economyReportCrawlService.crawlByDate(targetDate));
                case DEBENTURE -> crawlSingle(target, debentureReportCrawlService.crawlByDate(targetDate));
            };
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("selectedReportType", target.value);
            redirectAttributes.addFlashAttribute("selectedStockName", normalizedStockName);
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
            return "redirect:/admin";
        }

        CrawlResult result = executionResult.result();
        logCrawlResult(target, result);

        redirectAttributes.addFlashAttribute("crawlResult", result);
        redirectAttributes.addFlashAttribute("crawlTypeSavedCounts", executionResult.savedCounts());
        redirectAttributes.addFlashAttribute("selectedReportType", target.value);
        redirectAttributes.addFlashAttribute("selectedStockName", normalizedStockName);
        redirectAttributes.addFlashAttribute("message",
                target.label + " 수집 완료: 저장 " + result.savedCount() + "건, 중복 " + result.skippedCount() + "건");
        return "redirect:/admin";
    }

    private CrawlExecutionResult crawlSingle(CrawlTarget target, CrawlResult result) {
        return new CrawlExecutionResult(result, List.of(new CrawlTypeSavedCount(target.label, result.savedCount())));
    }

    private CrawlExecutionResult crawlAllByDate(LocalDate targetDate) {
        List<CrawlTypeSavedCount> savedCounts = new ArrayList<>();

        CrawlResult stockResult = reportCrawlService.crawlByDate(targetDate);
        logCrawlResult(CrawlTarget.STOCK, stockResult);
        addSavedCount(savedCounts, CrawlTarget.STOCK, stockResult);
        CrawlResult industryResult = industryReportCrawlService.crawlByDate(targetDate);
        logCrawlResult(CrawlTarget.INDUSTRY, industryResult);
        addSavedCount(savedCounts, CrawlTarget.INDUSTRY, industryResult);
        CrawlResult marketInfoResult = marketInfoReportCrawlService.crawlByDate(targetDate);
        logCrawlResult(CrawlTarget.MARKET_INFO, marketInfoResult);
        addSavedCount(savedCounts, CrawlTarget.MARKET_INFO, marketInfoResult);
        CrawlResult investResult = investReportCrawlService.crawlByDate(targetDate);
        logCrawlResult(CrawlTarget.INVEST, investResult);
        addSavedCount(savedCounts, CrawlTarget.INVEST, investResult);
        CrawlResult economyResult = economyReportCrawlService.crawlByDate(targetDate);
        logCrawlResult(CrawlTarget.ECONOMY, economyResult);
        addSavedCount(savedCounts, CrawlTarget.ECONOMY, economyResult);
        CrawlResult debentureResult = debentureReportCrawlService.crawlByDate(targetDate);
        logCrawlResult(CrawlTarget.DEBENTURE, debentureResult);
        addSavedCount(savedCounts, CrawlTarget.DEBENTURE, debentureResult);

        CrawlResult totalResult = merge(
                targetDate,
                stockResult,
                industryResult,
                marketInfoResult,
                investResult,
                economyResult,
                debentureResult
        );
        return new CrawlExecutionResult(totalResult, savedCounts);
    }

    private CrawlResult merge(LocalDate targetDate, CrawlResult... results) {
        int savedCount = 0;
        int skippedCount = 0;
        int failedCount = 0;
        int visitedPages = 0;

        for (CrawlResult result : results) {
            savedCount += result.savedCount();
            skippedCount += result.skippedCount();
            failedCount += result.failedCount();
            visitedPages += result.visitedPages();
        }

        return new CrawlResult(targetDate, savedCount, skippedCount, failedCount, visitedPages);
    }

    private void logCrawlResult(CrawlTarget target, CrawlResult result) {
        log.info(
                "Report crawl result: reportType={}, targetDate={}, savedCount={}, skippedCount={}, failedCount={}, visitedPages={}",
                target.value,
                result.targetDate(),
                result.savedCount(),
                result.skippedCount(),
                result.failedCount(),
                result.visitedPages()
        );
    }

    private void addSavedCount(List<CrawlTypeSavedCount> savedCounts, CrawlTarget target, CrawlResult result) {
        savedCounts.add(new CrawlTypeSavedCount(target.label, result.savedCount()));
    }

    public record CrawlExecutionResult(
            CrawlResult result,
            List<CrawlTypeSavedCount> savedCounts
    ) {
    }

    public record CrawlTypeSavedCount(
            String label,
            int savedCount
    ) {
    }

    @PostMapping("/admin/market-caps/sync")
    public String syncMarketCaps(RedirectAttributes redirectAttributes) {
        SyncResult result = marketCapSyncService.sync();
        redirectAttributes.addFlashAttribute("syncResult", result);
        redirectAttributes.addFlashAttribute("message",
                "시가총액 동기화 완료: 신규 " + result.savedCount() + "건, 갱신 " + result.updatedCount() + "건");
        return "redirect:/admin";
    }

    private enum CrawlTarget {
        ALL("all", "전체 리포트"),
        STOCK("stock", "종목 리포트"),
        INDUSTRY("industry", "산업 리포트"),
        MARKET_INFO("marketInfo", "시황정보 리포트"),
        INVEST("invest", "투자정보 리포트"),
        ECONOMY("economy", "경제분석 리포트"),
        DEBENTURE("debenture", "채권분석 리포트");

        private final String value;
        private final String label;

        CrawlTarget(String value, String label) {
            this.value = value;
            this.label = label;
        }

        private static CrawlTarget from(String value) {
            for (CrawlTarget target : values()) {
                if (target.value.equals(value)) {
                    return target;
                }
            }
            throw new IllegalArgumentException("지원하지 않는 수집 대상입니다. reportType=" + value);
        }
    }
}
