package com.example.jusikreport.debenture.service;

import com.example.jusikreport.debenture.dto.DebentureReportListItem;
import com.example.jusikreport.debenture.dto.DebentureReportSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DebentureReportQueryService {

    Page<DebentureReportListItem> search(DebentureReportSearchCondition condition, Pageable pageable);
}
