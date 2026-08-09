package com.example.jusikreport.report.service;

import com.example.jusikreport.report.dto.CrawlResult;

import java.time.LocalDate;

public interface ReportCrawlService {

    CrawlResult crawlByDate(LocalDate targetDate);

    CrawlResult crawlByStockName(String stockName, LocalDate targetDate);
}
