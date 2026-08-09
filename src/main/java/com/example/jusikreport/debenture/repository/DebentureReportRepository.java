package com.example.jusikreport.debenture.repository;

import com.example.jusikreport.debenture.domain.DebentureReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;

public interface DebentureReportRepository extends JpaRepository<DebentureReport, Long>, JpaSpecificationExecutor<DebentureReport> {

    boolean existsByReportTitleAndSecuritiesFirmAndReportDateAndPdfUrl(
            String reportTitle,
            String securitiesFirm,
            LocalDate reportDate,
            String pdfUrl
    );
}
