package com.example.jusikreport.economy.service;

import com.example.jusikreport.economy.dto.EconomyReportListItem;
import com.example.jusikreport.economy.dto.EconomyReportSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EconomyReportQueryService {

    Page<EconomyReportListItem> search(EconomyReportSearchCondition condition, Pageable pageable);
}
