package com.example.jusikreport.industry.service;

import com.example.jusikreport.industry.dto.IndustryReportListItem;
import com.example.jusikreport.industry.dto.IndustryReportSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IndustryReportQueryService {

    Page<IndustryReportListItem> search(IndustryReportSearchCondition condition, Pageable pageable);
}
