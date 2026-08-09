package com.example.jusikreport.economy.service;

import com.example.jusikreport.report.dto.CrawlResult;

import java.time.LocalDate;

public interface EconomyReportCrawlService {

    CrawlResult crawlByDate(LocalDate targetDate);
}
