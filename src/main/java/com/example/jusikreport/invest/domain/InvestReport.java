package com.example.jusikreport.invest.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "invest_reports",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_invest_report_unique",
                        columnNames = {"report_title", "securities_firm", "report_date", "pdf_url"}
                )
        }
)
public class InvestReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_title", nullable = false, length = 255)
    private String reportTitle;

    @Column(name = "securities_firm", nullable = false, length = 100)
    private String securitiesFirm;

    @Column(name = "pdf_url", length = 500)
    private String pdfUrl;

    @Column(name = "detail_url", length = 500)
    private String detailUrl;

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    @Column(name = "view_count")
    private Integer viewCount;

    @Column(name = "source_page")
    private Integer sourcePage;

    @Column(name = "is_read")
    private Boolean read;

    @Column(name = "is_important")
    private Boolean important;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected InvestReport() {
    }

    public InvestReport(
            String reportTitle,
            String securitiesFirm,
            String pdfUrl,
            String detailUrl,
            LocalDate reportDate,
            Integer viewCount,
            Integer sourcePage
    ) {
        this.reportTitle = reportTitle;
        this.securitiesFirm = securitiesFirm;
        this.pdfUrl = pdfUrl;
        this.detailUrl = detailUrl;
        this.reportDate = reportDate;
        this.viewCount = viewCount;
        this.sourcePage = sourcePage;
        this.read = Boolean.FALSE;
        this.important = Boolean.FALSE;
    }

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public String getSecuritiesFirm() {
        return securitiesFirm;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public boolean isRead() {
        return Boolean.TRUE.equals(read);
    }

    public void updateRead(boolean read) {
        this.read = read;
    }

    public boolean isImportant() {
        return Boolean.TRUE.equals(important);
    }

    public void updateImportant(boolean important) {
        this.important = important;
    }
}
