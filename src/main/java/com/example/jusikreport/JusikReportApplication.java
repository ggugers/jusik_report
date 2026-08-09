package com.example.jusikreport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class JusikReportApplication {

    private static final String DEFAULT_DB_PATH = "./data/jusik-report.db";

    public static void main(String[] args) {
        ensureSqliteParentDirectoryExists();
        SpringApplication.run(JusikReportApplication.class, args);
    }

    private static void ensureSqliteParentDirectoryExists() {
        String dbPath = System.getenv("APP_DB_PATH");
        if (dbPath == null || dbPath.isBlank()) {
            dbPath = DEFAULT_DB_PATH;
        }

        Path dbFilePath = Paths.get(dbPath).toAbsolutePath().normalize();
        Path parent = dbFilePath.getParent();
        if (parent == null) {
            return;
        }

        try {
            Files.createDirectories(parent);
        } catch (IOException exception) {
            throw new IllegalStateException("SQLite 데이터 디렉터리를 생성할 수 없습니다: " + parent, exception);
        }
    }
}
