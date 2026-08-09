package com.example.jusikreport.marketinfo.controller;

import com.example.jusikreport.marketinfo.domain.MarketInfoReport;
import com.example.jusikreport.marketinfo.dto.MarketInfoReportListItem;
import com.example.jusikreport.marketinfo.dto.MarketInfoReportSearchCondition;
import com.example.jusikreport.marketinfo.repository.MarketInfoReportRepository;
import com.example.jusikreport.marketinfo.service.MarketInfoReportQueryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.Map;

@Controller
public class MarketInfoReportController {

    private final MarketInfoReportQueryService queryService;
    private final MarketInfoReportRepository repository;

    public MarketInfoReportController(MarketInfoReportQueryService queryService, MarketInfoReportRepository repository) {
        this.queryService = queryService;
        this.repository = repository;
    }

    @GetMapping("/market-info-reports")
    public String marketInfoReports(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String reportTitle,
            @RequestParam(required = false) String securitiesFirm,
            @RequestParam(required = false) Boolean read,
            @RequestParam(required = false) Boolean important,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "reportDate") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            HttpServletRequest request,
            Model model
    ) {
        boolean hasDateParameter = request.getParameterMap().containsKey("startDate")
                || request.getParameterMap().containsKey("endDate");
        LocalDate searchStartDate = hasDateParameter ? startDate : LocalDate.now();
        LocalDate searchEndDate = hasDateParameter ? endDate : LocalDate.now();
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, 100, Sort.by(sortDirection, sort));
        MarketInfoReportSearchCondition condition = new MarketInfoReportSearchCondition(
                searchStartDate,
                searchEndDate,
                reportTitle,
                securitiesFirm,
                read,
                important
        );
        Page<MarketInfoReportListItem> result = queryService.search(condition, pageable);

        model.addAttribute("result", result);
        model.addAttribute("condition", condition);
        model.addAttribute("sort", sort);
        model.addAttribute("direction", sortDirection.name().toLowerCase());
        return "marketinfo/list";
    }

    @PostMapping("/market-info-reports/{id}/read")
    @ResponseBody
    public Map<String, Object> updateRead(@PathVariable Long id, @RequestBody Map<String, Boolean> payload) {
        MarketInfoReport report = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("시황정보 리포트를 찾을 수 없습니다. id=" + id));
        boolean read = Boolean.TRUE.equals(payload.get("read"));
        report.updateRead(read);
        repository.save(report);
        return Map.of("id", id, "read", read);
    }

    @PostMapping("/market-info-reports/{id}/important")
    @ResponseBody
    public Map<String, Object> updateImportant(@PathVariable Long id, @RequestBody Map<String, Boolean> payload) {
        MarketInfoReport report = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("시황정보 리포트를 찾을 수 없습니다. id=" + id));
        boolean important = Boolean.TRUE.equals(payload.get("important"));
        report.updateImportant(important);
        repository.save(report);
        return Map.of("id", id, "important", important);
    }
}
