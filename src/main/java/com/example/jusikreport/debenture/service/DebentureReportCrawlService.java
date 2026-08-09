package com.example.jusikreport.debenture.service;

import com.example.jusikreport.report.dto.CrawlResult;

import java.time.LocalDate;

public interface DebentureReportCrawlService {

    CrawlResult crawlByDate(LocalDate targetDate);
}
