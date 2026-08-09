package com.example.jusikreport.industry.repository;

import com.example.jusikreport.industry.domain.IndustryReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;

public interface IndustryReportRepository extends JpaRepository<IndustryReport, Long>, JpaSpecificationExecutor<IndustryReport> {

    boolean existsByCategoryNameAndReportTitleAndSecuritiesFirmAndReportDateAndPdfUrl(
            String categoryName,
            String reportTitle,
            String securitiesFirm,
            LocalDate reportDate,
            String pdfUrl
    );
}
