package com.example.jusikreport.debenture.service;

import com.example.jusikreport.debenture.dto.DebentureReportListItem;
import com.example.jusikreport.debenture.dto.DebentureReportSearchCondition;
import com.example.jusikreport.debenture.repository.DebentureReportRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DebentureReportQueryServiceImpl implements DebentureReportQueryService {

    private final DebentureReportRepository repository;

    public DebentureReportQueryServiceImpl(DebentureReportRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<DebentureReportListItem> search(DebentureReportSearchCondition condition, Pageable pageable) {
        List<DebentureReportListItem> sortedItems = repository.findAll(DebentureReportSpecification.from(condition)).stream()
                .map(DebentureReportListItem::from)
                .sorted(buildComparator(pageable))
                .toList();

        int start = Math.toIntExact(pageable.getOffset());
        int end = Math.min(start + pageable.getPageSize(), sortedItems.size());
        List<DebentureReportListItem> pageContent = start >= sortedItems.size() ? List.of() : sortedItems.subList(start, end);
        return new PageImpl<>(pageContent, pageable, sortedItems.size());
    }

    private Comparator<DebentureReportListItem> buildComparator(Pageable pageable) {
        Comparator<DebentureReportListItem> comparator = null;
        for (org.springframework.data.domain.Sort.Order order : pageable.getSort()) {
            Comparator<DebentureReportListItem> current = switch (order.getProperty()) {
                case "reportTitle" -> Comparator.comparing(DebentureReportListItem::reportTitle, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                case "securitiesFirm" -> Comparator.comparing(DebentureReportListItem::securitiesFirm, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                case "viewCount" -> Comparator.comparing(DebentureReportListItem::viewCount, Comparator.nullsLast(Integer::compareTo));
                case "reportDate" -> Comparator.comparing(DebentureReportListItem::reportDate, Comparator.nullsLast(Comparator.naturalOrder()));
                default -> Comparator.comparing(DebentureReportListItem::reportDate, Comparator.nullsLast(Comparator.naturalOrder()));
            };
            if (order.isDescending()) {
                current = current.reversed();
            }
            comparator = comparator == null ? current : comparator.thenComparing(current);
        }
        if (comparator == null) {
            comparator = Comparator.comparing(DebentureReportListItem::reportDate, Comparator.nullsLast(Comparator.reverseOrder()));
        }
        return comparator.thenComparing(DebentureReportListItem::id, Comparator.nullsLast(Comparator.reverseOrder()));
    }
}
