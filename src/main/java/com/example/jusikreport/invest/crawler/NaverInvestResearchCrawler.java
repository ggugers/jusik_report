package com.example.jusikreport.invest.crawler;

import com.example.jusikreport.invest.dto.CrawledInvestReport;
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
public class NaverInvestResearchCrawler {

    private static final String BASE_URL = "https://finance.naver.com";
    private static final String LIST_URL = BASE_URL + "/research/invest_list.naver";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36";
    private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("yy.MM.dd"),
            DateTimeFormatter.ofPattern("yyyy.MM.dd")
    );

    public List<CrawledInvestReport> crawlPage(int page) {
        try {
            Document document = Jsoup.connect(LIST_URL)
                    .data("page", String.valueOf(page))
                    .userAgent(USER_AGENT)
                    .timeout(10000)
                    .get();
            return parse(document);
        } catch (IOException exception) {
            throw new IllegalStateException("네이버 투자정보 리포트 페이지 조회에 실패했습니다. page=" + page, exception);
        }
    }

    private List<CrawledInvestReport> parse(Document document) {
        List<CrawledInvestReport> items = new ArrayList<>();
        Elements rows = document.select("table.type_1 tr");

        for (Element row : rows) {
            Elements tds = row.select("> td");
            if (tds.size() < 5) {
                continue;
            }

            Element titleLink = tds.get(0).selectFirst("a[href*=invest_read.naver]");
            if (titleLink == null) {
                continue;
            }

            Element pdfLink = tds.get(2).selectFirst("a[href]");
            items.add(new CrawledInvestReport(
                    titleLink.text().trim(),
                    tds.get(1).text().trim(),
                    pdfLink == null ? null : pdfLink.absUrl("href"),
                    titleLink.absUrl("href"),
                    parseDate(tds.get(3).text()),
                    parseInteger(tds.get(4).text())
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
        throw new IllegalArgumentException("투자정보 리포트 날짜 파싱 실패: " + value);
    }

    private Integer parseInteger(String value) {
        String digits = value.replace(",", "").trim();
        return digits.isEmpty() ? null : Integer.parseInt(digits);
    }
}
