package com.example.jusikreport.economy.repository;

import com.example.jusikreport.economy.domain.EconomyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;

public interface EconomyReportRepository extends JpaRepository<EconomyReport, Long>, JpaSpecificationExecutor<EconomyReport> {

    boolean existsByReportTitleAndSecuritiesFirmAndReportDateAndPdfUrl(
            String reportTitle,
            String securitiesFirm,
            LocalDate reportDate,
            String pdfUrl
    );
}
