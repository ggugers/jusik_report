package com.example.jusikreport.industry.service;

import com.example.jusikreport.industry.domain.IndustryReport;
import com.example.jusikreport.industry.dto.IndustryReportListItem;
import com.example.jusikreport.industry.dto.IndustryReportSearchCondition;
import com.example.jusikreport.industry.repository.IndustryReportRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class IndustryReportQueryServiceImpl implements IndustryReportQueryService {

    private final IndustryReportRepository repository;

    public IndustryReportQueryServiceImpl(IndustryReportRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<IndustryReportListItem> search(IndustryReportSearchCondition condition, Pageable pageable) {
        List<IndustryReportListItem> sortedItems = repository.findAll(IndustryReportSpecification.from(condition)).stream()
                .map(IndustryReportListItem::from)
                .sorted(buildComparator(pageable))
                .toList();

        int start = Math.toIntExact(pageable.getOffset());
        int end = Math.min(start + pageable.getPageSize(), sortedItems.size());
        List<IndustryReportListItem> pageContent = start >= sortedItems.size() ? List.of() : sortedItems.subList(start, end);
        return new PageImpl<>(pageContent, pageable, sortedItems.size());
    }

    private Comparator<IndustryReportListItem> buildComparator(Pageable pageable) {
        Comparator<IndustryReportListItem> comparator = null;
        for (org.springframework.data.domain.Sort.Order order : pageable.getSort()) {
            Comparator<IndustryReportListItem> current = switch (order.getProperty()) {
                case "categoryName" -> Comparator.comparing(IndustryReportListItem::categoryName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                case "reportTitle" -> Comparator.comparing(IndustryReportListItem::reportTitle, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                case "securitiesFirm" -> Comparator.comparing(IndustryReportListItem::securitiesFirm, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                case "reportDate" -> Comparator.comparing(IndustryReportListItem::reportDate, Comparator.nullsLast(Comparator.naturalOrder()));
                default -> Comparator.comparing(IndustryReportListItem::reportDate, Comparator.nullsLast(Comparator.naturalOrder()));
            };
            if (order.isDescending()) {
                current = current.reversed();
            }
            comparator = comparator == null ? current : comparator.thenComparing(current);
        }
        if (comparator == null) {
            comparator = Comparator.comparing(IndustryReportListItem::reportDate, Comparator.nullsLast(Comparator.reverseOrder()));
        }
        return comparator.thenComparing(IndustryReportListItem::id, Comparator.nullsLast(Comparator.reverseOrder()));
    }
}
