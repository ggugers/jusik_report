package com.example.jusikreport.invest.service;

import com.example.jusikreport.report.dto.CrawlResult;

import java.time.LocalDate;

public interface InvestReportCrawlService {

    CrawlResult crawlByDate(LocalDate targetDate);
}
