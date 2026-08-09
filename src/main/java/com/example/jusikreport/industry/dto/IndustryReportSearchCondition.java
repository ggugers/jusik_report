package com.example.jusikreport.industry.dto;

import java.time.LocalDate;

public record IndustryReportSearchCondition(
        LocalDate startDate,
        LocalDate endDate,
        String categoryName,
        String securitiesFirm,
        Boolean read,
        Boolean important
) {
    public String normalizedCategoryName() {
        return categoryName == null ? null : categoryName.trim();
    }

    public String normalizedSecuritiesFirm() {
        return securitiesFirm == null ? null : securitiesFirm.trim();
    }
}
