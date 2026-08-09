package com.example.jusikreport.report.crawler;

import com.example.jusikreport.report.dto.CrawledReport;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class NaverResearchCrawler {

    private static final Logger log = LoggerFactory.getLogger(NaverResearchCrawler.class);

    private static final String BASE_URL = "https://finance.naver.com";
    private static final String LIST_URL = BASE_URL + "/research/company_list.naver";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36";
    private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("yy.MM.dd"),
            DateTimeFormatter.ofPattern("yyyy.MM.dd")
    );

    public List<CrawledReport> crawlPage(int page) {
        String url = UriComponentsBuilder.fromUriString(LIST_URL)
                .queryParam("page", page)
                .toUriString();
        log.info("Crawl Naver company research URL: {}", url);

        try {
            Document document = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(10000)
                    .get();

            return parse(document);
        } catch (IOException exception) {
            throw new IllegalStateException("네이버 리포트 페이지 조회에 실패했습니다. page=" + page, exception);
        }
    }

    public List<CrawledReport> crawlPageByItemCode(String itemCode, int page) {
        String url = UriComponentsBuilder.fromUriString(LIST_URL)
                .queryParam("searchType", "itemCode")
                .queryParam("itemCode", itemCode)
                .queryParam("page", page)
                .toUriString();
        log.info("Crawl Naver company research URL: {}", url);

        try {
            Document document = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(10000)
                    .get();

            return parse(document);
        } catch (IOException exception) {
            throw new IllegalStateException("?ㅼ씠踰?醫낅ぉ 由ы룷???섏씠吏 議고쉶???ㅽ뙣?덉뒿?덂. itemCode=" + itemCode + ", page=" + page, exception);
        }
    }

    private List<CrawledReport> parse(Document document) {
        List<CrawledReport> items = new ArrayList<>();
        Elements rows = document.select("table.type_1 tr");

        for (Element row : rows) {
            Elements tds = row.select("> td");
            if (tds.size() < 6) {
                continue;
            }

            Element stockLink = tds.get(0).selectFirst("a.stock_item");
            Element titleLink = tds.get(1).selectFirst("a[href*=company_read.naver]");
            Element pdfLink = tds.get(3).selectFirst("a[href]");

            if (stockLink == null || titleLink == null) {
                continue;
            }

            String stockUrl = stockLink.absUrl("href");
            String stockCode = extractQueryParam(stockUrl, "code");
            String stockName = stockLink.text().trim();
            String reportTitle = titleLink.text().trim();
            String securitiesFirm = tds.get(2).text().trim();
            String pdfUrl = pdfLink == null ? "" : pdfLink.absUrl("href");
            String detailUrl = titleLink.absUrl("href");
            LocalDate reportDate = parseDate(tds.get(4).text());
            Integer viewCount = parseInteger(tds.get(5).text());

            items.add(new CrawledReport(
                    stockCode,
                    stockName,
                    reportTitle,
                    securitiesFirm,
                    pdfUrl,
                    detailUrl,
                    reportDate,
                    viewCount
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
        throw new IllegalArgumentException("리포트 날짜 파싱 실패: " + value);
    }

    private Integer parseInteger(String value) {
        String digits = value.replace(",", "").trim();
        return digits.isEmpty() ? null : Integer.parseInt(digits);
    }

    private String extractQueryParam(String url, String name) {
        return UriComponentsBuilder.fromUriString(url).build().getQueryParams().getFirst(name);
    }
}
