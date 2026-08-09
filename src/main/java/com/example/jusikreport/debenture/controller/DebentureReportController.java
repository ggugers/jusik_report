package com.example.jusikreport.debenture.controller;

import com.example.jusikreport.debenture.domain.DebentureReport;
import com.example.jusikreport.debenture.dto.DebentureReportListItem;
import com.example.jusikreport.debenture.dto.DebentureReportSearchCondition;
import com.example.jusikreport.debenture.repository.DebentureReportRepository;
import com.example.jusikreport.debenture.service.DebentureReportQueryService;
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
public class DebentureReportController {

    private final DebentureReportQueryService queryService;
    private final DebentureReportRepository repository;

    public DebentureReportController(DebentureReportQueryService queryService, DebentureReportRepository repository) {
        this.queryService = queryService;
        this.repository = repository;
    }

    @GetMapping("/debenture-reports")
    public String debentureReports(
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
        DebentureReportSearchCondition condition = new DebentureReportSearchCondition(
                searchStartDate,
                searchEndDate,
                reportTitle,
                securitiesFirm,
                read,
                important
        );
        Page<DebentureReportListItem> result = queryService.search(condition, pageable);

        model.addAttribute("result", result);
        model.addAttribute("condition", condition);
        model.addAttribute("sort", sort);
        model.addAttribute("direction", sortDirection.name().toLowerCase());
        return "debenture/list";
    }

    @PostMapping("/debenture-reports/{id}/read")
    @ResponseBody
    public Map<String, Object> updateRead(@PathVariable Long id, @RequestBody Map<String, Boolean> payload) {
        DebentureReport report = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("채권분석 리포트를 찾을 수 없습니다. id=" + id));
        boolean read = Boolean.TRUE.equals(payload.get("read"));
        report.updateRead(read);
        repository.save(report);
        return Map.of("id", id, "read", read);
    }

    @PostMapping("/debenture-reports/{id}/important")
    @ResponseBody
    public Map<String, Object> updateImportant(@PathVariable Long id, @RequestBody Map<String, Boolean> payload) {
        DebentureReport report = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("채권분석 리포트를 찾을 수 없습니다. id=" + id));
        boolean important = Boolean.TRUE.equals(payload.get("important"));
        report.updateImportant(important);
        repository.save(report);
        return Map.of("id", id, "important", important);
    }
}
