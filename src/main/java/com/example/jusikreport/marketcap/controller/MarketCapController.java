package com.example.jusikreport.marketcap.controller;

import com.example.jusikreport.marketcap.domain.MarketCap;
import com.example.jusikreport.marketcap.dto.MarketCapSearchCondition;
import com.example.jusikreport.marketcap.service.MarketCapQueryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

@Controller
public class MarketCapController {

    private static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of(
            "rankNo",
            "stockCode",
            "stockName",
            "marketType",
            "currentPrice",
            "priceChange",
            "changeRate",
            "faceValue",
            "marketCapValue",
            "listedShares",
            "foreignRatio",
            "volume",
            "per",
            "roe",
            "pbr",
            "operatingProfitGrowthRate",
            "updatedAt"
    );

    private final MarketCapQueryService marketCapQueryService;

    public MarketCapController(MarketCapQueryService marketCapQueryService) {
        this.marketCapQueryService = marketCapQueryService;
    }

    @GetMapping("/market-caps")
    public String list(
            @RequestParam(required = false) String stockName,
            @RequestParam(required = false) String marketType,
            @RequestParam(required = false) Long minMarketCap,
            @RequestParam(required = false) Long maxMarketCap,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "rankNo") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request,
            Model model
    ) {
        boolean hasSortParameter = request.getParameterMap().containsKey("sort");
        String sortField = ALLOWED_SORT_PROPERTIES.contains(sort) ? sort : "rankNo";
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, 100, Sort.by(sortDirection, sortField));
        MarketCapSearchCondition condition = new MarketCapSearchCondition(stockName, marketType, minMarketCap, maxMarketCap);
        Page<MarketCap> result = marketCapQueryService.search(condition, pageable);

        model.addAttribute("result", result);
        model.addAttribute("condition", condition);
        model.addAttribute("sort", sortField);
        model.addAttribute("direction", sortDirection.name().toLowerCase());
        model.addAttribute("explicitSort", hasSortParameter);
        return "marketcap/list";
    }
}
