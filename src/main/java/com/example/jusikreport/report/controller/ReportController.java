package com.example.jusikreport.report.controller;

import com.example.jusikreport.report.domain.Report;
import com.example.jusikreport.report.domain.TargetPriceChange;
import com.example.jusikreport.report.dto.ReportListItem;
import com.example.jusikreport.report.dto.ReportSearchCondition;
import com.example.jusikreport.report.repository.ReportRepository;
import com.example.jusikreport.report.service.ReportQueryService;
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
public class ReportController {

    private final ReportQueryService reportQueryService;
    private final ReportRepository reportRepository;

    public ReportController(ReportQueryService reportQueryService, ReportRepository reportRepository) {
        this.reportQueryService = reportQueryService;
        this.reportRepository = reportRepository;
    }

    @GetMapping("/reports")
    public String reports(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String stockName,
            @RequestParam(required = false) String securitiesFirm,
            @RequestParam(required = false) Boolean read,
            @RequestParam(required = false) Boolean important,
            @RequestParam(required = false) TargetPriceChange targetPriceChange,
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
        boolean hasSortParameter = request.getParameterMap().containsKey("sort");
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = hasSortParameter
                ? PageRequest.of(page, 100, Sort.by(sortDirection, sort))
                : PageRequest.of(page, 100);
        ReportSearchCondition condition = new ReportSearchCondition(searchStartDate, searchEndDate, stockName, securitiesFirm, read, important, targetPriceChange);
        Page<ReportListItem> result = reportQueryService.search(condition, pageable);

        model.addAttribute("result", result);
        model.addAttribute("condition", condition);
        model.addAttribute("targetPriceChanges", TargetPriceChange.values());
        model.addAttribute("sort", hasSortParameter ? sort : "reportDate");
        model.addAttribute("direction", hasSortParameter ? sortDirection.name().toLowerCase() : "desc");
        model.addAttribute("explicitSort", hasSortParameter);
        return "report/list";
    }

    @PostMapping("/reports/{id}/read")
    @ResponseBody
    public Map<String, Object> updateRead(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> payload
    ) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("리포트를 찾을 수 없습니다. id=" + id));

        boolean read = Boolean.TRUE.equals(payload.get("read"));
        report.updateRead(read);
        reportRepository.save(report);

        return Map.of(
                "id", id,
                "read", read
        );
    }

    @PostMapping("/reports/{id}/important")
    @ResponseBody
    public Map<String, Object> updateImportant(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> payload
    ) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("리포트를 찾을 수 없습니다. id=" + id));

        boolean important = Boolean.TRUE.equals(payload.get("important"));
        report.updateImportant(important);
        reportRepository.save(report);

        return Map.of(
                "id", id,
                "important", important
        );
    }

    @PostMapping("/reports/{id}/target-price-change")
    @ResponseBody
    public Map<String, Object> updateTargetPriceChange(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload
    ) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("리포트를 찾을 수 없습니다. id=" + id));

        TargetPriceChange targetPriceChange = parseTargetPriceChange(payload.get("targetPriceChange"));
        report.updateTargetPriceChange(targetPriceChange);
        reportRepository.save(report);

        return Map.of(
                "id", id,
                "targetPriceChange", targetPriceChange == null ? "" : targetPriceChange.name()
        );
    }

    private TargetPriceChange parseTargetPriceChange(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return TargetPriceChange.valueOf(value);
    }
}
