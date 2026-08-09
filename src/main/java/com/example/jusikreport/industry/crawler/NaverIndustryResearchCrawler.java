package com.example.jusikreport.industry.crawler;

import com.example.jusikreport.industry.dto.CrawledIndustryReport;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class NaverIndustryResearchCrawler {

    private static final String BASE_URL = "https://finance.naver.com";
    private static final String LIST_URL = BASE_URL + "/research/industry_list.naver";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36";
    private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("yy.MM.dd"),
            DateTimeFormatter.ofPattern("yyyy.MM.dd")
    );

    public List<CrawledIndustryReport> crawlPage(int page) {
        try {
            Document document = Jsoup.connect(LIST_URL)
                    .data("page", String.valueOf(page))
                    .userAgent(USER_AGENT)
                    .timeout(10000)
                    .get();
            return parse(document);
        } catch (IOException exception) {
            throw new IllegalStateException("네이버 산업 리포트 페이지 조회에 실패했습니다. page=" + page, exception);
        }
    }

    private List<CrawledIndustryReport> parse(Document document) {
        List<CrawledIndustryReport> items = new ArrayList<>();
        Elements rows = document.select("table.type_1 tr");

        for (Element row : rows) {
            Elements tds = row.select("> td");
            if (tds.size() < 6) {
                continue;
            }

            Element titleLink = tds.get(1).selectFirst("a[href*=industry_read.naver]");
            if (titleLink == null) {
                continue;
            }

            Element pdfLink = tds.get(3).selectFirst("a[href]");

            items.add(new CrawledIndustryReport(
                    tds.get(0).text().trim(),
                    titleLink.text().trim(),
                    tds.get(2).text().trim(),
                    pdfLink == null ? null : pdfLink.absUrl("href"),
                    titleLink.absUrl("href"),
                    parseDate(tds.get(4).text()),
                    parseInteger(tds.get(5).text())
            ));
        }

        return items;
    }

    private LocalDate parseDate(String value) {
        String normalized = value.trim();
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(normalized, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }
        throw new IllegalArgumentException("산업 리포트 날짜 파싱 실패: " + value);
    }

    private Integer parseInteger(String value) {
        String digits = value.replace(",", "").trim();
        return digits.isEmpty() ? null : Integer.parseInt(digits);
    }
}
