package com.example.jusikreport.report.service;

import com.example.jusikreport.report.domain.Report;
import com.example.jusikreport.report.dto.ReportSearchCondition;
import org.springframework.data.jpa.domain.Specification;

public final class ReportSpecification {

    private ReportSpecification() {
    }

    public static Specification<Report> from(ReportSearchCondition condition) {
        return Specification.where(hasStartDate(condition))
                .and(hasEndDate(condition))
                .and(hasStockName(condition))
                .and(hasSecuritiesFirm(condition))
                .and(hasRead(condition))
                .and(hasImportant(condition))
                .and(hasTargetPriceChange(condition));
    }

    private static Specification<Report> hasStartDate(ReportSearchCondition condition) {
        return (root, query, criteriaBuilder) ->
                condition.startDate() == null ? null : criteriaBuilder.greaterThanOrEqualTo(root.get("reportDate"), condition.startDate());
    }

    private static Specification<Report> hasEndDate(ReportSearchCondition condition) {
        return (root, query, criteriaBuilder) ->
                condition.endDate() == null ? null : criteriaBuilder.lessThanOrEqualTo(root.get("reportDate"), condition.endDate());
    }

    private static Specification<Report> hasStockName(ReportSearchCondition condition) {
        return (root, query, criteriaBuilder) -> {
            String stockName = condition.normalizedStockName();
            return stockName == null || stockName.isBlank()
                    ? null
                    : criteriaBuilder.like(root.get("stockName"), "%" + stockName + "%");
        };
    }

    private static Specification<Report> hasSecuritiesFirm(ReportSearchCondition condition) {
        return (root, query, criteriaBuilder) -> {
            String securitiesFirm = condition.normalizedSecuritiesFirm();
            return securitiesFirm == null || securitiesFirm.isBlank()
                    ? null
                    : criteriaBuilder.like(root.get("securitiesFirm"), "%" + securitiesFirm + "%");
        };
    }

    private static Specification<Report> hasRead(ReportSearchCondition condition) {
        return (root, query, criteriaBuilder) ->
                condition.read() == null ? null : criteriaBuilder.equal(root.get("read"), condition.read());
    }

    private static Specification<Report> hasImportant(ReportSearchCondition condition) {
        return (root, query, criteriaBuilder) ->
                condition.important() == null ? null : criteriaBuilder.equal(root.get("important"), condition.important());
    }

    private static Specification<Report> hasTargetPriceChange(ReportSearchCondition condition) {
        return (root, query, criteriaBuilder) ->
                condition.targetPriceChange() == null
                        ? null
                        : criteriaBuilder.equal(root.get("targetPriceChange"), condition.targetPriceChange());
    }
}
