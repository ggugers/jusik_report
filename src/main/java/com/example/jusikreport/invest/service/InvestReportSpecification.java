package com.example.jusikreport.invest.service;

import com.example.jusikreport.invest.domain.InvestReport;
import com.example.jusikreport.invest.dto.InvestReportSearchCondition;
import org.springframework.data.jpa.domain.Specification;

public final class InvestReportSpecification {

    private InvestReportSpecification() {
    }

    public static Specification<InvestReport> from(InvestReportSearchCondition condition) {
        return Specification.where(hasStartDate(condition))
                .and(hasEndDate(condition))
                .and(hasReportTitle(condition))
                .and(hasSecuritiesFirm(condition))
                .and(hasRead(condition))
                .and(hasImportant(condition));
    }

    private static Specification<InvestReport> hasStartDate(InvestReportSearchCondition condition) {
        return (root, query, cb) -> condition.startDate() == null ? null : cb.greaterThanOrEqualTo(root.get("reportDate"), condition.startDate());
    }

    private static Specification<InvestReport> hasEndDate(InvestReportSearchCondition condition) {
        return (root, query, cb) -> condition.endDate() == null ? null : cb.lessThanOrEqualTo(root.get("reportDate"), condition.endDate());
    }

    private static Specification<InvestReport> hasReportTitle(InvestReportSearchCondition condition) {
        return (root, query, cb) -> {
            String reportTitle = condition.normalizedReportTitle();
            return reportTitle == null || reportTitle.isBlank() ? null : cb.like(root.get("reportTitle"), "%" + reportTitle + "%");
        };
    }

    private static Specification<InvestReport> hasSecuritiesFirm(InvestReportSearchCondition condition) {
        return (root, query, cb) -> {
            String securitiesFirm = condition.normalizedSecuritiesFirm();
            return securitiesFirm == null || securitiesFirm.isBlank() ? null : cb.like(root.get("securitiesFirm"), "%" + securitiesFirm + "%");
        };
    }

    private static Specification<InvestReport> hasRead(InvestReportSearchCondition condition) {
        return (root, query, cb) -> condition.read() == null ? null : cb.equal(root.get("read"), condition.read());
    }

    private static Specification<InvestReport> hasImportant(InvestReportSearchCondition condition) {
        return (root, query, cb) -> condition.important() == null ? null : cb.equal(root.get("important"), condition.important());
    }
}
