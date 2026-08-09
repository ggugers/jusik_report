package com.example.jusikreport.invest.repository;

import com.example.jusikreport.invest.domain.InvestReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;

public interface InvestReportRepository extends JpaRepository<InvestReport, Long>, JpaSpecificationExecutor<InvestReport> {

    boolean existsByReportTitleAndSecuritiesFirmAndReportDateAndPdfUrl(
            String reportTitle,
            String securitiesFirm,
            LocalDate reportDate,
            String pdfUrl
    );
}
