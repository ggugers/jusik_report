package com.example.jusikreport.report.repository;

import com.example.jusikreport.report.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;

public interface ReportRepository extends JpaRepository<Report, Long>, JpaSpecificationExecutor<Report> {

    boolean existsByStockCodeAndReportTitleAndSecuritiesFirmAndReportDateAndPdfUrl(
            String stockCode,
            String reportTitle,
            String securitiesFirm,
            LocalDate reportDate,
            String pdfUrl
    );
}
