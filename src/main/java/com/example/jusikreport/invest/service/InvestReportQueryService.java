package com.example.jusikreport.invest.service;

import com.example.jusikreport.invest.dto.InvestReportListItem;
import com.example.jusikreport.invest.dto.InvestReportSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InvestReportQueryService {

    Page<InvestReportListItem> search(InvestReportSearchCondition condition, Pageable pageable);
}
