package com.example.jusikreport.debenture.crawler;

import com.example.jusikreport.debenture.crawler.dto.NaverDebentureResearchDetail;
import com.example.jusikreport.debenture.crawler.dto.NaverDebentureResearchItem;
import com.example.jusikreport.debenture.crawler.dto.NaverDebentureResearchResponse;
import com.example.jusikreport.debenture.dto.CrawledDebentureReport;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class NaverDebentureResearchCrawler {

    private static final Logger log = LoggerFactory.getLogger(NaverDebentureResearchCrawler.class);

    private static final String BASE_URL = "https://stock.naver.com";
    private static final String LIST_API_URL = BASE_URL + "/api/stockSecurity/researches/v2/debenture";
    private static final String DETAIL_API_URL_TEMPLATE = BASE_URL + "/api/stockSecurity/researches/v2/debenture/{nid}";
    private static final String DETAIL_WEB_URL_TEMPLATE = BASE_URL + "/research/debenture/{nid}";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36";
    private static final int DEFAULT_PAGE_SIZE = 50;

    private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE,         // yyyy-MM-dd
            DateTimeFormatter.ofPattern("yyyy.MM.dd"),
            DateTimeFormatter.ofPattern("yy.MM.dd"),
            DateTimeFormatter.ofPattern("yyyyMMdd")
    );

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final ExecutorService executorService;

    public NaverDebentureResearchCrawler() {
        this(null, null);
    }

    public NaverDebentureResearchCrawler(RestClient.Builder restClientBuilder, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper != null ? objectMapper : new ObjectMapper()
                .findAndRegisterModules()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(10));
        requestFactory.setReadTimeout(Duration.ofSeconds(15));

        RestClient.Builder builder = restClientBuilder != null ? restClientBuilder : RestClient.builder();
        this.restClient = builder
                .requestFactory(requestFactory)
                .defaultHeader(HttpHeaders.USER_AGENT, USER_AGENT)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();

        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
    }

    /**
     * 일자별 채권분석 리포트 목록을 페이지 번호(1-based)로 조회합니다.
     */
    public List<CrawledDebentureReport> crawlPage(LocalDate targetDate, int page) {
        return crawlPage(targetDate, targetDate, page, DEFAULT_PAGE_SIZE);
    }

    /**
     * 기존 호환: 날짜 제한 없이 페이지 번호(1-based)로 조회합니다.
     */
    public List<CrawledDebentureReport> crawlPage(int page) {
        return crawlPage(null, null, page, DEFAULT_PAGE_SIZE);
    }

    /**
     * 사용자가 제공한 웹 URL (예: https://stock.naver.com/research/debenture?startDate=20261015&endDate=20261015)
     * 기반으로 파라미터를 추출하여 페이지 번호(1-based)로 조회합니다.
     */
    public List<CrawledDebentureReport> crawlByWebUrl(String webUrl, int page) {
        UriComponentsBuilder ucb = UriComponentsBuilder.fromUriString(webUrl);
        var params = ucb.build().getQueryParams();

        LocalDate startDate = parseDateParam(params.getFirst("startDate"));
        LocalDate endDate = parseDateParam(params.getFirst("endDate"));

        return crawlPage(startDate, endDate, page, DEFAULT_PAGE_SIZE);
    }

    /**
     * 네이버 증권 신규 채권 리서치 REST API를 호출하여 리포트 목록과 각 리포트의 상세 PDF 첨부 URL을 수집합니다.
     *
     * @param startDate 시작일 (포함, null 허용)
     * @param endDate   종료일 (포함, null 허용)
     * @param page      페이지 번호 (1-based, 내부적으로 0-based index 변환)
     * @param size      페이지 크기
     * @return 수집된 채권분석 리포트 리스트
     */
    public List<CrawledDebentureReport> crawlPage(LocalDate startDate, LocalDate endDate, int page, int size) {
        int index = Math.max(0, page - 1);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(LIST_API_URL)
                .queryParam("index", index)
                .queryParam("size", size);

        if (startDate != null) {
            uriBuilder.queryParam("startDate", startDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        if (endDate != null) {
            uriBuilder.queryParam("endDate", endDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        URI requestUri = uriBuilder.build().encode().toUri();
        log.info("Crawl Naver debenture research API: {}", requestUri);

        try {
            String responseBody = restClient.get()
                    .uri(requestUri)
                    .retrieve()
                    .body(String.class);

            if (responseBody == null || responseBody.isBlank()) {
                log.warn("Empty response received from Naver debenture research API: {}", requestUri);
                return List.of();
            }

            NaverDebentureResearchResponse response = objectMapper.readValue(responseBody, NaverDebentureResearchResponse.class);
            List<NaverDebentureResearchItem> items = response.safeItems();

            if (items.isEmpty()) {
                log.info("No debenture research items returned from API for page={}, index={}", page, index);
                return List.of();
            }

            // 가상 스레드를 활용해 각 리포트의 상세 정보(attachUrl/PDF 링크)를 병렬로 안전하게 조회
            List<CompletableFuture<CrawledDebentureReport>> futures = items.stream()
                    .map(item -> CompletableFuture.supplyAsync(() -> toCrawledDebentureReport(item), executorService))
                    .toList();

            List<CrawledDebentureReport> results = new ArrayList<>();
            for (CompletableFuture<CrawledDebentureReport> future : futures) {
                try {
                    results.add(future.join());
                } catch (Exception e) {
                    log.error("Failed to map debenture research report item", e);
                }
            }

            return results;
        } catch (Exception exception) {
            throw new IllegalStateException("네이버 채권분석 리포트 API 조회에 실패했습니다. uri=" + requestUri, exception);
        }
    }

    /**
     * 리포트 상세 API를 호출하여 PDF 파일 첨부 링크(attachUrl)를 가져옵니다.
     */
    public String fetchAttachUrl(String nid) {
        if (nid == null || nid.isBlank()) {
            return "";
        }
        URI detailUri = UriComponentsBuilder.fromUriString(DETAIL_API_URL_TEMPLATE)
                .buildAndExpand(nid.trim())
                .encode()
                .toUri();

        try {
            String responseBody = restClient.get()
                    .uri(detailUri)
                    .retrieve()
                    .body(String.class);

            if (responseBody != null && !responseBody.isBlank()) {
                NaverDebentureResearchDetail detail = objectMapper.readValue(responseBody, NaverDebentureResearchDetail.class);
                return detail.attachUrl() != null ? detail.attachUrl() : "";
            }
        } catch (Exception e) {
            log.warn("Failed to fetch debenture research detail attachUrl for nid: {}. Error: {}", nid, e.getMessage());
        }
        return "";
    }

    private CrawledDebentureReport toCrawledDebentureReport(NaverDebentureResearchItem item) {
        String nid = item.nid() != null ? item.nid().trim() : "";
        String detailUrl = nid.isEmpty() ? "" : DETAIL_WEB_URL_TEMPLATE.replace("{nid}", nid);
        String pdfUrl = nid.isEmpty() ? "" : fetchAttachUrl(nid);

        LocalDate reportDate = parseDate(item.writeDate());
        Integer viewCount = parseInteger(item.readCount());

        return new CrawledDebentureReport(
                item.title() != null ? item.title().trim() : "",
                item.brokerName() != null ? item.brokerName().trim() : "",
                pdfUrl,
                detailUrl,
                reportDate,
                viewCount
        );
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return LocalDate.now();
        }
        String normalized = value.trim();
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(normalized, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }
        throw new IllegalArgumentException("채권분석 리포트 날짜 파싱 실패: " + value);
    }

    private LocalDate parseDateParam(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String clean = value.replaceAll("[^0-9-]", "").trim();
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(clean, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }
        return null;
    }

    private Integer parseInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String digits = value.replace(",", "").trim();
        return digits.isEmpty() ? null : Integer.parseInt(digits);
    }
}
