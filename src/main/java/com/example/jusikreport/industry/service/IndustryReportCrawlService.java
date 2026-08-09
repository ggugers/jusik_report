package com.example.jusikreport.industry.service;

import com.example.jusikreport.report.dto.CrawlResult;

import java.time.LocalDate;

public interface IndustryReportCrawlService {

    CrawlResult crawlByDate(LocalDate targetDate);
}
