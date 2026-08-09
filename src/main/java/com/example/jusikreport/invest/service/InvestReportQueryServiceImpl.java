package com.example.jusikreport.invest.service;

import com.example.jusikreport.invest.dto.InvestReportListItem;
import com.example.jusikreport.invest.dto.InvestReportSearchCondition;
import com.example.jusikreport.invest.repository.InvestReportRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class InvestReportQueryServiceImpl implements InvestReportQueryService {

    private final InvestReportRepository repository;

    public InvestReportQueryServiceImpl(InvestReportRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<InvestReportListItem> search(InvestReportSearchCondition condition, Pageable pageable) {
        List<InvestReportListItem> sortedItems = repository.findAll(InvestReportSpecification.from(condition)).stream()
                .map(InvestReportListItem::from)
                .sorted(buildComparator(pageable))
                .toList();

        int start = Math.toIntExact(pageable.getOffset());
        int end = Math.min(start + pageable.getPageSize(), sortedItems.size());
        List<InvestReportListItem> pageContent = start >= sortedItems.size() ? List.of() : sortedItems.subList(start, end);
        return new PageImpl<>(pageContent, pageable, sortedItems.size());
    }

    private Comparator<InvestReportListItem> buildComparator(Pageable pageable) {
        Comparator<InvestReportListItem> comparator = null;
        for (org.springframework.data.domain.Sort.Order order : pageable.getSort()) {
            Comparator<InvestReportListItem> current = switch (order.getProperty()) {
                case "reportTitle" -> Comparator.comparing(InvestReportListItem::reportTitle, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                case "securitiesFirm" -> Comparator.comparing(InvestReportListItem::securitiesFirm, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                case "viewCount" -> Comparator.comparing(InvestReportListItem::viewCount, Comparator.nullsLast(Integer::compareTo));
                case "reportDate" -> Comparator.comparing(InvestReportListItem::reportDate, Comparator.nullsLast(Comparator.naturalOrder()));
                default -> Comparator.comparing(InvestReportListItem::reportDate, Comparator.nullsLast(Comparator.naturalOrder()));
            };
            if (order.isDescending()) {
                current = current.reversed();
            }
            comparator = comparator == null ? current : comparator.thenComparing(current);
        }
        if (comparator == null) {
            comparator = Comparator.comparing(InvestReportListItem::reportDate, Comparator.nullsLast(Comparator.reverseOrder()));
        }
        return comparator.thenComparing(InvestReportListItem::id, Comparator.nullsLast(Comparator.reverseOrder()));
    }
}
