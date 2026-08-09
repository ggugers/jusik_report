package com.example.jusikreport.debenture.service;

import com.example.jusikreport.debenture.domain.DebentureReport;
import com.example.jusikreport.debenture.dto.DebentureReportSearchCondition;
import org.springframework.data.jpa.domain.Specification;

public final class DebentureReportSpecification {

    private DebentureReportSpecification() {
    }

    public static Specification<DebentureReport> from(DebentureReportSearchCondition condition) {
        return Specification.where(hasStartDate(condition))
                .and(hasEndDate(condition))
                .and(hasReportTitle(condition))
                .and(hasSecuritiesFirm(condition))
                .and(hasRead(condition))
                .and(hasImportant(condition));
    }

    private static Specification<DebentureReport> hasStartDate(DebentureReportSearchCondition condition) {
        return (root, query, cb) -> condition.startDate() == null ? null : cb.greaterThanOrEqualTo(root.get("reportDate"), condition.startDate());
    }

    private static Specification<DebentureReport> hasEndDate(DebentureReportSearchCondition condition) {
        return (root, query, cb) -> condition.endDate() == null ? null : cb.lessThanOrEqualTo(root.get("reportDate"), condition.endDate());
    }

    private static Specification<DebentureReport> hasReportTitle(DebentureReportSearchCondition condition) {
        return (root, query, cb) -> {
            String reportTitle = condition.normalizedReportTitle();
            return reportTitle == null || reportTitle.isBlank() ? null : cb.like(root.get("reportTitle"), "%" + reportTitle + "%");
        };
    }

    private static Specification<DebentureReport> hasSecuritiesFirm(DebentureReportSearchCondition condition) {
        return (root, query, cb) -> {
            String securitiesFirm = condition.normalizedSecuritiesFirm();
            return securitiesFirm == null || securitiesFirm.isBlank() ? null : cb.like(root.get("securitiesFirm"), "%" + securitiesFirm + "%");
        };
    }

    private static Specification<DebentureReport> hasRead(DebentureReportSearchCondition condition) {
        return (root, query, cb) -> condition.read() == null ? null : cb.equal(root.get("read"), condition.read());
    }

    private static Specification<DebentureReport> hasImportant(DebentureReportSearchCondition condition) {
        return (root, query, cb) -> condition.important() == null ? null : cb.equal(root.get("important"), condition.important());
    }
}
