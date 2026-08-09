package com.example.jusikreport.marketinfo.service;

import com.example.jusikreport.marketinfo.dto.MarketInfoReportListItem;
import com.example.jusikreport.marketinfo.dto.MarketInfoReportSearchCondition;
import com.example.jusikreport.marketinfo.repository.MarketInfoReportRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MarketInfoReportQueryServiceImpl implements MarketInfoReportQueryService {

    private final MarketInfoReportRepository repository;

    public MarketInfoReportQueryServiceImpl(MarketInfoReportRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<MarketInfoReportListItem> search(MarketInfoReportSearchCondition condition, Pageable pageable) {
        List<MarketInfoReportListItem> sortedItems = repository.findAll(MarketInfoReportSpecification.from(condition)).stream()
                .map(MarketInfoReportListItem::from)
                .sorted(buildComparator(pageable))
                .toList();

        int start = Math.toIntExact(pageable.getOffset());
        int end = Math.min(start + pageable.getPageSize(), sortedItems.size());
        List<MarketInfoReportListItem> pageContent = start >= sortedItems.size() ? List.of() : sortedItems.subList(start, end);
        return new PageImpl<>(pageContent, pageable, sortedItems.size());
    }

    private Comparator<MarketInfoReportListItem> buildComparator(Pageable pageable) {
        Comparator<MarketInfoReportListItem> comparator = null;
        for (org.springframework.data.domain.Sort.Order order : pageable.getSort()) {
            Comparator<MarketInfoReportListItem> current = switch (order.getProperty()) {
                case "reportTitle" -> Comparator.comparing(MarketInfoReportListItem::reportTitle, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                case "securitiesFirm" -> Comparator.comparing(MarketInfoReportListItem::securitiesFirm, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                case "viewCount" -> Comparator.comparing(MarketInfoReportListItem::viewCount, Comparator.nullsLast(Integer::compareTo));
                case "reportDate" -> Comparator.comparing(MarketInfoReportListItem::reportDate, Comparator.nullsLast(Comparator.naturalOrder()));
                default -> Comparator.comparing(MarketInfoReportListItem::reportDate, Comparator.nullsLast(Comparator.naturalOrder()));
            };
            if (order.isDescending()) {
                current = current.reversed();
            }
            comparator = comparator == null ? current : comparator.thenComparing(current);
        }
        if (comparator == null) {
            comparator = Comparator.comparing(MarketInfoReportListItem::reportDate, Comparator.nullsLast(Comparator.reverseOrder()));
        }
        return comparator.thenComparing(MarketInfoReportListItem::id, Comparator.nullsLast(Comparator.reverseOrder()));
    }
}
