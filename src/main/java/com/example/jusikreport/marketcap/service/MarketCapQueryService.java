package com.example.jusikreport.marketcap.service;

import com.example.jusikreport.marketcap.domain.MarketCap;
import com.example.jusikreport.marketcap.dto.MarketCapSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MarketCapQueryService {
    Page<MarketCap> search(MarketCapSearchCondition condition, Pageable pageable);
}
