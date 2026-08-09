package com.example.jusikreport.report.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
        name = "reports",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_report_unique",
                        columnNames = {"stock_code", "report_title", "securities_firm", "report_date", "pdf_url"}
                )
        }
)
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stock_code", length = 20)
    private String stockCode;

    @Column(name = "stock_name", nullable = false, length = 100)
    private String stockName;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "target_price_change", length = 20)
    private TargetPriceChange targetPriceChange;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Report() {
    }

    public Report(
            String stockCode,
            String stockName,
            String reportTitle,
            String securitiesFirm,
            String pdfUrl,
            String detailUrl,
            LocalDate reportDate,
            Integer viewCount,
            Integer sourcePage
    ) {
        this.stockCode = stockCode;
        this.stockName = stockName;
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

    public String getStockCode() {
        return stockCode;
    }

    public String getStockName() {
        return stockName;
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

    public Integer getSourcePage() {
        return sourcePage;
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

    public TargetPriceChange getTargetPriceChange() {
        return targetPriceChange;
    }

    public void updateTargetPriceChange(TargetPriceChange targetPriceChange) {
        this.targetPriceChange = targetPriceChange;
    }
}
