package com.example.jusikreport.marketcap.controller;

import com.example.jusikreport.marketcap.domain.MarketCap;
import com.example.jusikreport.marketcap.dto.MarketCapSearchCondition;
import com.example.jusikreport.marketcap.service.MarketCapQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(MarketCapController.class)
class MarketCapControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MarketCapQueryService marketCapQueryService;

    @Test
    @DisplayName("전체 주식 (시가총액 목록) 페이지 요청 성공 및 정렬 파라미터 바인딩")
    void testMarketCapList() throws Exception {
        MarketCap sample = new MarketCap("005930", "삼성전자", "KOSPI", "450조", 450000000000000L, 1);
        given(marketCapQueryService.search(any(MarketCapSearchCondition.class), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(sample)));

        mockMvc.perform(get("/market-caps")
                        .param("stockName", "삼성")
                        .param("marketType", "KOSPI")
                        .param("sort", "marketCapValue")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(view().name("marketcap/list"))
                .andExpect(model().attributeExists("result"))
                .andExpect(model().attribute("sort", "marketCapValue"))
                .andExpect(model().attribute("direction", "desc"));
    }
}
