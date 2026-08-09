package com.example.jusikreport.marketinfo.service;

import com.example.jusikreport.marketinfo.dto.MarketInfoReportListItem;
import com.example.jusikreport.marketinfo.dto.MarketInfoReportSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MarketInfoReportQueryService {

    Page<MarketInfoReportListItem> search(MarketInfoReportSearchCondition condition, Pageable pageable);
}
