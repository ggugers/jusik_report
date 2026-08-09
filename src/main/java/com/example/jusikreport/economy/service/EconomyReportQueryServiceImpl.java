package com.example.jusikreport.economy.service;

import com.example.jusikreport.economy.dto.EconomyReportListItem;
import com.example.jusikreport.economy.dto.EconomyReportSearchCondition;
import com.example.jusikreport.economy.repository.EconomyReportRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class EconomyReportQueryServiceImpl implements EconomyReportQueryService {

    private final EconomyReportRepository repository;

    public EconomyReportQueryServiceImpl(EconomyReportRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<EconomyReportListItem> search(EconomyReportSearchCondition condition, Pageable pageable) {
        List<EconomyReportListItem> sortedItems = repository.findAll(EconomyReportSpecification.from(condition)).stream()
                .map(EconomyReportListItem::from)
                .sorted(buildComparator(pageable))
                .toList();

        int start = Math.toIntExact(pageable.getOffset());
        int end = Math.min(start + pageable.getPageSize(), sortedItems.size());
        List<EconomyReportListItem> pageContent = start >= sortedItems.size() ? List.of() : sortedItems.subList(start, end);
        return new PageImpl<>(pageContent, pageable, sortedItems.size());
    }

    private Comparator<EconomyReportListItem> buildComparator(Pageable pageable) {
        Comparator<EconomyReportListItem> comparator = null;
        for (org.springframework.data.domain.Sort.Order order : pageable.getSort()) {
            Comparator<EconomyReportListItem> current = switch (order.getProperty()) {
                case "reportTitle" -> Comparator.comparing(EconomyReportListItem::reportTitle, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                case "securitiesFirm" -> Comparator.comparing(EconomyReportListItem::securitiesFirm, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                case "viewCount" -> Comparator.comparing(EconomyReportListItem::viewCount, Comparator.nullsLast(Integer::compareTo));
                case "reportDate" -> Comparator.comparing(EconomyReportListItem::reportDate, Comparator.nullsLast(Comparator.naturalOrder()));
                default -> Comparator.comparing(EconomyReportListItem::reportDate, Comparator.nullsLast(Comparator.naturalOrder()));
            };
            if (order.isDescending()) {
                current = current.reversed();
            }
            comparator = comparator == null ? current : comparator.thenComparing(current);
        }
        if (comparator == null) {
            comparator = Comparator.comparing(EconomyReportListItem::reportDate, Comparator.nullsLast(Comparator.reverseOrder()));
        }
        return comparator.thenComparing(EconomyReportListItem::id, Comparator.nullsLast(Comparator.reverseOrder()));
    }
}
