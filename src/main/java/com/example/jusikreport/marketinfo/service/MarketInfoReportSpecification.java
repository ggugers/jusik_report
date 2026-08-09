package com.example.jusikreport.marketinfo.service;

import com.example.jusikreport.marketinfo.domain.MarketInfoReport;
import com.example.jusikreport.marketinfo.dto.MarketInfoReportSearchCondition;
import org.springframework.data.jpa.domain.Specification;

public final class MarketInfoReportSpecification {

    private MarketInfoReportSpecification() {
    }

    public static Specification<MarketInfoReport> from(MarketInfoReportSearchCondition condition) {
        return Specification.where(hasStartDate(condition))
                .and(hasEndDate(condition))
                .and(hasReportTitle(condition))
                .and(hasSecuritiesFirm(condition))
                .and(hasRead(condition))
                .and(hasImportant(condition));
    }

    private static Specification<MarketInfoReport> hasStartDate(MarketInfoReportSearchCondition condition) {
        return (root, query, cb) -> condition.startDate() == null ? null : cb.greaterThanOrEqualTo(root.get("reportDate"), condition.startDate());
    }

    private static Specification<MarketInfoReport> hasEndDate(MarketInfoReportSearchCondition condition) {
        return (root, query, cb) -> condition.endDate() == null ? null : cb.lessThanOrEqualTo(root.get("reportDate"), condition.endDate());
    }

    private static Specification<MarketInfoReport> hasReportTitle(MarketInfoReportSearchCondition condition) {
        return (root, query, cb) -> {
            String reportTitle = condition.normalizedReportTitle();
            return reportTitle == null || reportTitle.isBlank() ? null : cb.like(root.get("reportTitle"), "%" + reportTitle + "%");
        };
    }

    private static Specification<MarketInfoReport> hasSecuritiesFirm(MarketInfoReportSearchCondition condition) {
        return (root, query, cb) -> {
            String securitiesFirm = condition.normalizedSecuritiesFirm();
            return securitiesFirm == null || securitiesFirm.isBlank() ? null : cb.like(root.get("securitiesFirm"), "%" + securitiesFirm + "%");
        };
    }

    private static Specification<MarketInfoReport> hasRead(MarketInfoReportSearchCondition condition) {
        return (root, query, cb) -> condition.read() == null ? null : cb.equal(root.get("read"), condition.read());
    }

    private static Specification<MarketInfoReport> hasImportant(MarketInfoReportSearchCondition condition) {
        return (root, query, cb) -> condition.important() == null ? null : cb.equal(root.get("important"), condition.important());
    }
}
