package com.example.jusikreport.marketinfo.repository;

import com.example.jusikreport.marketinfo.domain.MarketInfoReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;

public interface MarketInfoReportRepository extends JpaRepository<MarketInfoReport, Long>, JpaSpecificationExecutor<MarketInfoReport> {

    boolean existsByReportTitleAndSecuritiesFirmAndReportDateAndPdfUrl(
            String reportTitle,
            String securitiesFirm,
            LocalDate reportDate,
            String pdfUrl
    );
}
