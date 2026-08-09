package com.example.jusikreport.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Component
public class SqliteSchemaInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public SqliteSchemaInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        ensureMarketTypeColumn();
        ensureReportReadColumn();
        ensureReportImportantColumn();
        ensureReportTargetPriceChangeColumn();
        ensureIndustryReportReadColumn();
        ensureIndustryReportImportantColumn();
        normalizeReportDateColumns();
    }

    private void ensureMarketTypeColumn() {
        if (!tableExists("market_caps")) {
            return;
        }

        if (columnExists("market_caps", "market_type")) {
            return;
        }

        jdbcTemplate.execute("ALTER TABLE market_caps ADD COLUMN market_type VARCHAR(20) NOT NULL DEFAULT 'KOSPI'");
        jdbcTemplate.update("UPDATE market_caps SET market_type = 'KOSPI' WHERE market_type IS NULL OR market_type = ''");
    }

    private void ensureReportReadColumn() {
        if (!tableExists("reports")) {
            return;
        }

        if (columnExists("reports", "is_read")) {
            return;
        }

        jdbcTemplate.execute("ALTER TABLE reports ADD COLUMN is_read BOOLEAN NOT NULL DEFAULT 0");
        jdbcTemplate.update("UPDATE reports SET is_read = 0 WHERE is_read IS NULL");
    }

    private void ensureReportImportantColumn() {
        if (!tableExists("reports")) {
            return;
        }

        if (columnExists("reports", "is_important")) {
            return;
        }

        jdbcTemplate.execute("ALTER TABLE reports ADD COLUMN is_important BOOLEAN NOT NULL DEFAULT 0");
        jdbcTemplate.update("UPDATE reports SET is_important = 0 WHERE is_important IS NULL");
    }

    private void ensureReportTargetPriceChangeColumn() {
        if (!tableExists("reports")) {
            return;
        }

        if (columnExists("reports", "target_price_change")) {
            return;
        }

        jdbcTemplate.execute("ALTER TABLE reports ADD COLUMN target_price_change VARCHAR(20)");
    }

    private void ensureIndustryReportReadColumn() {
        if (!tableExists("industry_reports")) {
            return;
        }

        if (columnExists("industry_reports", "is_read")) {
            return;
        }

        jdbcTemplate.execute("ALTER TABLE industry_reports ADD COLUMN is_read BOOLEAN NOT NULL DEFAULT 0");
        jdbcTemplate.update("UPDATE industry_reports SET is_read = 0 WHERE is_read IS NULL");
    }

    private void ensureIndustryReportImportantColumn() {
        if (!tableExists("industry_reports")) {
            return;
        }

        if (columnExists("industry_reports", "is_important")) {
            return;
        }

        jdbcTemplate.execute("ALTER TABLE industry_reports ADD COLUMN is_important BOOLEAN NOT NULL DEFAULT 0");
        jdbcTemplate.update("UPDATE industry_reports SET is_important = 0 WHERE is_important IS NULL");
    }

    private void normalizeReportDateColumns() {
        List<String> tables = List.of(
                "reports",
                "industry_reports",
                "market_info_reports",
                "invest_reports",
                "economy_reports",
                "debenture_reports"
        );

        for (String table : tables) {
            normalizeReportDateColumn(table);
        }
    }

    private void normalizeReportDateColumn(String tableName) {
        if (!tableExists(tableName) || !columnExists(tableName, "report_date")) {
            return;
        }

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT id, report_date FROM " + tableName + " " +
                        "WHERE typeof(report_date) = 'integer' " +
                        "OR (length(CAST(report_date AS TEXT)) > 10 AND CAST(report_date AS TEXT) NOT LIKE '%-%')"
        );

        for (Map<String, Object> row : rows) {
            Object id = row.get("id");
            Object value = row.get("report_date");
            if (id == null || value == null) {
                continue;
            }

            LocalDate reportDate = Instant.ofEpochMilli(Long.parseLong(value.toString()))
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            jdbcTemplate.update(
                    "UPDATE " + tableName + " SET report_date = ? WHERE id = ?",
                    reportDate.toString(),
                    id
            );
        }
    }

    private boolean tableExists(String tableName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sqlite_master WHERE type = 'table' AND name = ?",
                Integer.class,
                tableName
        );
        return count != null && count > 0;
    }

    private boolean columnExists(String tableName, String columnName) {
        List<String> columns = jdbcTemplate.query(
                "PRAGMA table_info(" + tableName + ")",
                (rs, rowNum) -> rs.getString("name")
        );
        return columns.stream().anyMatch(columnName::equalsIgnoreCase);
    }
}
