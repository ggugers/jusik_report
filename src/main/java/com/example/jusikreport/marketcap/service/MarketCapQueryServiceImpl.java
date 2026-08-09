package com.example.jusikreport.marketcap.service;

import com.example.jusikreport.marketcap.domain.MarketCap;
import com.example.jusikreport.marketcap.dto.MarketCapSearchCondition;
import com.example.jusikreport.marketcap.repository.MarketCapRepository;
import com.example.jusikreport.marketcap.repository.MarketCapSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MarketCapQueryServiceImpl implements MarketCapQueryService {

    private final MarketCapRepository marketCapRepository;

    public MarketCapQueryServiceImpl(MarketCapRepository marketCapRepository) {
        this.marketCapRepository = marketCapRepository;
    }

    @Override
    public Page<MarketCap> search(MarketCapSearchCondition condition, Pageable pageable) {
        if (pageable != null && pageable.getSort().isSorted()) {
            Sort.Order order = pageable.getSort().iterator().next();
            String sortField = order.getProperty();
            Sort.Direction sortDirection = order.getDirection();
            Pageable unsortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
            return marketCapRepository.findAll(
                    MarketCapSpecification.from(condition, sortField, sortDirection),
                    unsortedPageable
            );
        }
        return marketCapRepository.findAll(MarketCapSpecification.from(condition), pageable);
    }
}
