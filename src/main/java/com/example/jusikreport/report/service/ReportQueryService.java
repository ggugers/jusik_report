package com.example.jusikreport.report.service;

import com.example.jusikreport.report.dto.ReportListItem;
import com.example.jusikreport.report.dto.ReportSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReportQueryService {

    Page<ReportListItem> search(ReportSearchCondition condition, Pageable pageable);
}
