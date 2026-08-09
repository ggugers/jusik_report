package com.example.jusikreport.marketinfo.service;

import com.example.jusikreport.report.dto.CrawlResult;

import java.time.LocalDate;

public interface MarketInfoReportCrawlService {

    CrawlResult crawlByDate(LocalDate targetDate);
}
