package com.example.jusikreport.economy.service;

import com.example.jusikreport.economy.domain.EconomyReport;
import com.example.jusikreport.economy.dto.EconomyReportSearchCondition;
import org.springframework.data.jpa.domain.Specification;

public final class EconomyReportSpecification {

    private EconomyReportSpecification() {
    }

    public static Specification<EconomyReport> from(EconomyReportSearchCondition condition) {
        return Specification.where(hasStartDate(condition))
                .and(hasEndDate(condition))
                .and(hasReportTitle(condition))
                .and(hasSecuritiesFirm(condition))
                .and(hasRead(condition))
                .and(hasImportant(condition));
    }

    private static Specification<EconomyReport> hasStartDate(EconomyReportSearchCondition condition) {
        return (root, query, cb) -> condition.startDate() == null ? null : cb.greaterThanOrEqualTo(root.get("reportDate"), condition.startDate());
    }

    private static Specification<EconomyReport> hasEndDate(EconomyReportSearchCondition condition) {
        return (root, query, cb) -> condition.endDate() == null ? null : cb.lessThanOrEqualTo(root.get("reportDate"), condition.endDate());
    }

    private static Specification<EconomyReport> hasReportTitle(EconomyReportSearchCondition condition) {
        return (root, query, cb) -> {
            String reportTitle = condition.normalizedReportTitle();
            return reportTitle == null || reportTitle.isBlank() ? null : cb.like(root.get("reportTitle"), "%" + reportTitle + "%");
        };
    }

    private static Specification<EconomyReport> hasSecuritiesFirm(EconomyReportSearchCondition condition) {
        return (root, query, cb) -> {
            String securitiesFirm = condition.normalizedSecuritiesFirm();
            return securitiesFirm == null || securitiesFirm.isBlank() ? null : cb.like(root.get("securitiesFirm"), "%" + securitiesFirm + "%");
        };
    }

    private static Specification<EconomyReport> hasRead(EconomyReportSearchCondition condition) {
        return (root, query, cb) -> condition.read() == null ? null : cb.equal(root.get("read"), condition.read());
    }

    private static Specification<EconomyReport> hasImportant(EconomyReportSearchCondition condition) {
        return (root, query, cb) -> condition.important() == null ? null : cb.equal(root.get("important"), condition.important());
    }
}
