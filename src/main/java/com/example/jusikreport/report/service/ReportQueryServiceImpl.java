package com.example.jusikreport.report.service;

import com.example.jusikreport.marketcap.domain.MarketCap;
import com.example.jusikreport.marketcap.repository.MarketCapRepository;
import com.example.jusikreport.report.domain.Report;
import com.example.jusikreport.report.dto.ReportListItem;
import com.example.jusikreport.report.dto.ReportSearchCondition;
import com.example.jusikreport.report.repository.ReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReportQueryServiceImpl implements ReportQueryService {

    private static final Logger log = LoggerFactory.getLogger(ReportQueryServiceImpl.class);

    private final ReportRepository reportRepository;
    private final MarketCapRepository marketCapRepository;

    public ReportQueryServiceImpl(ReportRepository reportRepository, MarketCapRepository marketCapRepository) {
        this.reportRepository = reportRepository;
        this.marketCapRepository = marketCapRepository;
    }

    @Override
    public Page<ReportListItem> search(ReportSearchCondition condition, Pageable pageable) {
        List<Report> reports = reportRepository.findAll(ReportSpecification.from(condition));
        log.info(
                "Search stock reports. startDate={}, endDate={}, stockName={}, securitiesFirm={}, read={}, important={}, targetPriceChange={}, matchedCount={}",
                condition.startDate(),
                condition.endDate(),
                condition.stockName(),
                condition.securitiesFirm(),
                condition.read(),
                condition.important(),
                condition.targetPriceChange(),
                reports.size()
        );

        Set<String> stockCodes = reports.stream()
                .map(Report::getStockCode)
                .filter(Objects::nonNull)
                .filter(value -> !value.isBlank())
                .collect(Collectors.toSet());

        Set<String> stockNames = reports.stream()
                .map(Report::getStockName)
                .filter(Objects::nonNull)
                .filter(value -> !value.isBlank())
                .collect(Collectors.toSet());

        List<MarketCap> marketCapsByCode = stockCodes.isEmpty()
                ? Collections.emptyList()
                : marketCapRepository.findByStockCodeIn(stockCodes);

        Map<String, MarketCap> marketCapByCode = new HashMap<>();
        for (MarketCap marketCap : marketCapsByCode) {
            marketCapByCode.put(marketCap.getStockCode(), marketCap);
        }

        List<MarketCap> marketCapsByName = stockNames.isEmpty()
                ? Collections.emptyList()
                : marketCapRepository.findByStockNameIn(stockNames);

        Map<String, MarketCap> marketCapByName = new HashMap<>();
        for (MarketCap marketCap : marketCapsByName) {
            marketCapByName.put(marketCap.getStockName(), marketCap);
        }

        List<ReportListItem> items = reports.stream()
                .map(report -> {
                    MarketCap marketCap = marketCapByCode.get(report.getStockCode());
                    if (marketCap == null) {
                        marketCap = marketCapByName.get(report.getStockName());
                    }
                    ReportListItem base = ReportListItem.from(
                            report,
                            marketCap == null ? null : marketCap.getRankNo(),
                            marketCap == null ? null : marketCap.getMarketCapText(),
                            marketCap == null ? null : marketCap.getMarketCapValue()
                    );
                    return base.withMarket(
                            marketCap == null ? null : marketCap.getMarketType(),
                            marketCap == null ? null : marketCap.getRankNo(),
                            marketCap == null ? null : marketCap.getMarketCapText(),
                            marketCap == null ? null : marketCap.getMarketCapValue()
                    );
                })
                .toList();

        List<ReportListItem> sortedItems = items.stream()
                .sorted(buildComparator(pageable))
                .toList();

        int start = Math.toIntExact(pageable.getOffset());
        int end = Math.min(start + pageable.getPageSize(), sortedItems.size());
        List<ReportListItem> pageContent = start >= sortedItems.size()
                ? Collections.emptyList()
                : sortedItems.subList(start, end);

        return new PageImpl<>(pageContent, pageable, sortedItems.size());
    }

    private Comparator<ReportListItem> buildComparator(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            return Comparator.comparing(ReportListItem::reportDate, Comparator.nullsLast(Comparator.reverseOrder()))
                    .thenComparing(ReportListItem::marketCapValue, Comparator.nullsLast(Comparator.reverseOrder()))
                    .thenComparing(ReportListItem::id, Comparator.nullsLast(Comparator.reverseOrder()));
        }

        Comparator<ReportListItem> comparator = null;

        for (org.springframework.data.domain.Sort.Order order : pageable.getSort()) {
            Comparator<ReportListItem> current = switch (order.getProperty()) {
                case "stockName" -> Comparator.comparing(ReportListItem::stockName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                case "marketCapRank" -> Comparator.comparing(ReportListItem::marketCapRank, Comparator.nullsLast(Integer::compareTo));
                case "marketCapValue" -> Comparator.comparing(ReportListItem::marketCapValue, Comparator.nullsLast(Long::compareTo));
                case "reportTitle" -> Comparator.comparing(ReportListItem::reportTitle, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                case "securitiesFirm" -> Comparator.comparing(ReportListItem::securitiesFirm, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                case "reportDate" -> Comparator.comparing(ReportListItem::reportDate, Comparator.nullsLast(Comparator.naturalOrder()));
                default -> Comparator.comparing(ReportListItem::reportDate, Comparator.nullsLast(Comparator.naturalOrder()));
            };

            if (order.isDescending()) {
                current = current.reversed();
            }

            comparator = comparator == null ? current : comparator.thenComparing(current);
        }

        return comparator.thenComparing(ReportListItem::id, Comparator.nullsLast(Comparator.reverseOrder()));
    }
}
