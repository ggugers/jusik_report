package com.example.jusikreport.marketcap.crawler;

import com.example.jusikreport.marketcap.dto.CrawledMarketCap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class NaverMarketCapCrawler {

    private static final String LIST_URL = "https://finance.naver.com/sise/sise_market_sum.naver";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36";

    private final MarketCapValueParser marketCapValueParser;

    public NaverMarketCapCrawler(MarketCapValueParser marketCapValueParser) {
        this.marketCapValueParser = marketCapValueParser;
    }

    public List<CrawledMarketCap> crawlPage(int page, Integer sosok, String marketType) {
        try {
            var connection = Jsoup.connect(LIST_URL)
                    .data("page", String.valueOf(page));

            if (sosok != null) {
                connection.data("sosok", String.valueOf(sosok));
            }

            Document document = connection
                    .userAgent(USER_AGENT)
            .timeout(10000)
                    .get();

            return parse(document, marketType);
        } catch (IOException exception) {
            throw new IllegalStateException("네이버 시가총액 페이지 조회에 실패했습니다. page=" + page, exception);
        }
    }

    private List<CrawledMarketCap> parse(Document document, String marketType) {
        List<CrawledMarketCap> items = new ArrayList<>();
        Elements rows = document.select("table.type_2 tr");

        for (Element row : rows) {
            Elements tds = row.select("> td");
            if (tds.size() < 12 || !tds.get(0).hasClass("no")) {
                continue;
            }

            Element stockLink = tds.get(1).selectFirst("a.tltle");
            if (stockLink == null) {
                continue;
            }

            String stockCode = extractQueryParam(stockLink.absUrl("href"), "code");
            String stockName = stockLink.text().trim();
            Integer rankNo = parseNullableInteger(tds.get(0).text());

            String currentPriceText = tds.get(2).text().trim();
            Long currentPrice = parseNullableLong(currentPriceText);

            String changeRateText = tds.get(4).text().trim();
            Double changeRate = parseNullableDouble(changeRateText);

            String rawPriceChangeText = tds.get(3).text().trim();
            Long priceChangeAbs = parseNullableLong(rawPriceChangeText);
            Long priceChange = null;
            String priceChangeText = rawPriceChangeText;

            if (priceChangeAbs != null) {
                if ((changeRate != null && changeRate < 0) || rawPriceChangeText.contains("하락")) {
                    priceChange = -Math.abs(priceChangeAbs);
                    priceChangeText = "-" + String.format("%,d", Math.abs(priceChangeAbs));
                } else if ((changeRate != null && changeRate > 0) || rawPriceChangeText.contains("상승")) {
                    priceChange = Math.abs(priceChangeAbs);
                    priceChangeText = "+" + String.format("%,d", Math.abs(priceChangeAbs));
                } else {
                    priceChange = 0L;
                    priceChangeText = "0";
                }
            }

            if (changeRate != null && !changeRateText.contains("%") && !changeRateText.isBlank()) {
                changeRateText = (changeRate > 0 ? "+" : "") + changeRateText + "%";
            }

            String faceValueText = tds.get(5).text().trim();
            Long faceValue = parseNullableLong(faceValueText);

            String marketCapText = tds.get(6).text().trim();
            Long marketCapValue = marketCapValueParser.parse(marketCapText);

            String listedSharesText = tds.get(7).text().trim();
            Long listedShares = parseNullableLong(listedSharesText);

            String foreignRatioText = tds.get(8).text().trim();
            Double foreignRatio = parseNullableDouble(foreignRatioText);

            String volumeText = tds.get(9).text().trim();
            Long volume = parseNullableLong(volumeText);

            String perText = tds.get(10).text().trim();
            Double per = parseNullableDouble(perText);

            String roeText = tds.get(11).text().trim();
            Double roe = parseNullableDouble(roeText);

            items.add(new CrawledMarketCap(
                    stockCode,
                    stockName,
                    marketType,
                    marketCapText,
                    marketCapValue,
                    rankNo,
                    currentPrice,
                    currentPriceText,
                    priceChange,
                    priceChangeText,
                    changeRate,
                    changeRateText,
                    faceValue,
                    faceValueText,
                    listedShares,
                    listedSharesText,
                    foreignRatio,
                    foreignRatioText,
                    volume,
                    volumeText,
                    per,
                    perText,
                    roe,
                    roeText
            ));
        }

        return items;
    }

    private Integer parseNullableInteger(String text) {
        Long val = parseNullableLong(text);
        return val == null ? null : val.intValue();
    }

    private Long parseNullableLong(String text) {
        if (text == null) return null;
        String clean = text.replaceAll("[^0-9-]", "");
        if (clean.isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(clean);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parseNullableDouble(String text) {
        if (text == null) return null;
        String clean = text.replace(",", "").replace("%", "").replace("+", "").trim();
        if (clean.isEmpty() || "-".equals(clean) || "N/A".equalsIgnoreCase(clean)) {
            return null;
        }
        try {
            return Double.parseDouble(clean);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String extractQueryParam(String url, String name) {
        return UriComponentsBuilder.fromUriString(url).build().getQueryParams().getFirst(name);
    }
}
