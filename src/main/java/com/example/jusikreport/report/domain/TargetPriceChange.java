package com.example.jusikreport.report.domain;

public enum TargetPriceChange {
    RAISE("상향"),
    MAINTAIN("유지"),
    LOWER("하향");

    private final String label;

    TargetPriceChange(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
