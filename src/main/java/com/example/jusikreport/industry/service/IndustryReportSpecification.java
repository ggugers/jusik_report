package com.example.jusikreport.industry.service;

import com.example.jusikreport.industry.domain.IndustryReport;
import com.example.jusikreport.industry.dto.IndustryReportSearchCondition;
import org.springframework.data.jpa.domain.Specification;

public final class IndustryReportSpecification {

    private IndustryReportSpecification() {
    }

    public static Specification<IndustryReport> from(IndustryReportSearchCondition condition) {
        return Specification.where(hasStartDate(condition))
                .and(hasEndDate(condition))
                .and(hasCategoryName(condition))
                .and(hasSecuritiesFirm(condition))
                .and(hasRead(condition))
                .and(hasImportant(condition));
    }

    private static Specification<IndustryReport> hasStartDate(IndustryReportSearchCondition condition) {
        return (root, query, cb) -> condition.startDate() == null ? null : cb.greaterThanOrEqualTo(root.get("reportDate"), condition.startDate());
    }

    private static Specification<IndustryReport> hasEndDate(IndustryReportSearchCondition condition) {
        return (root, query, cb) -> condition.endDate() == null ? null : cb.lessThanOrEqualTo(root.get("reportDate"), condition.endDate());
    }

    private static Specification<IndustryReport> hasCategoryName(IndustryReportSearchCondition condition) {
        return (root, query, cb) -> {
            String categoryName = condition.normalizedCategoryName();
            return categoryName == null || categoryName.isBlank() ? null : cb.like(root.get("categoryName"), "%" + categoryName + "%");
        };
    }

    private static Specification<IndustryReport> hasSecuritiesFirm(IndustryReportSearchCondition condition) {
        return (root, query, cb) -> {
            String securitiesFirm = condition.normalizedSecuritiesFirm();
            return securitiesFirm == null || securitiesFirm.isBlank() ? null : cb.like(root.get("securitiesFirm"), "%" + securitiesFirm + "%");
        };
    }

    private static Specification<IndustryReport> hasRead(IndustryReportSearchCondition condition) {
        return (root, query, cb) -> condition.read() == null ? null : cb.equal(root.get("read"), condition.read());
    }

    private static Specification<IndustryReport> hasImportant(IndustryReportSearchCondition condition) {
        return (root, query, cb) -> condition.important() == null ? null : cb.equal(root.get("important"), condition.important());
    }
}
