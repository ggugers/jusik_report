# 1. Build Stage
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests -B

# 2. Runtime Stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create data directory for SQLite DB storage
RUN mkdir -p /app/data

# Copy built JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

ENV APP_DB_PATH=/app/data/jusik-report.db

EXPOSE 8080

ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "app.jar"]
