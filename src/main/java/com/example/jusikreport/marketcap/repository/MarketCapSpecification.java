package com.example.jusikreport.marketcap.repository;

import com.example.jusikreport.marketcap.domain.MarketCap;
import com.example.jusikreport.marketcap.dto.MarketCapSearchCondition;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class MarketCapSpecification {

    private MarketCapSpecification() {
    }

    public static Specification<MarketCap> from(MarketCapSearchCondition condition) {
        return from(condition, null, null);
    }

    public static Specification<MarketCap> from(MarketCapSearchCondition condition, String sortField, Sort.Direction sortDirection) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (condition != null) {
                if (condition.stockName() != null && !condition.stockName().isBlank()) {
                    String keyword = "%" + condition.stockName().trim().toLowerCase() + "%";
                    predicates.add(cb.or(
                            cb.like(cb.lower(root.get("stockName")), keyword),
                            cb.like(cb.lower(root.get("stockCode")), keyword)
                    ));
                }

                if (condition.marketType() != null && !condition.marketType().isBlank()) {
                    predicates.add(cb.equal(root.get("marketType"), condition.marketType().trim()));
                }

                if (condition.minMarketCap() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("marketCapValue"), condition.minMarketCap()));
                }

                if (condition.maxMarketCap() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("marketCapValue"), condition.maxMarketCap()));
                }
            }

            if (sortField != null && !sortField.isBlank() && sortDirection != null) {
                Class<?> resultType = query.getResultType();
                if (resultType != Long.class && resultType != long.class) {
                    Expression<?> sortExpression = root.get(sortField);
                    Order nullsLastOrder = cb.asc(cb.selectCase().when(cb.isNull(sortExpression), 1).otherwise(0));
                    Order valueOrder = sortDirection.isAscending()
                            ? cb.asc(sortExpression)
                            : cb.desc(sortExpression);

                    query.orderBy(nullsLastOrder, valueOrder);
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

