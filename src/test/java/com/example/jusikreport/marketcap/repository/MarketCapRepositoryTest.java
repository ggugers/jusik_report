package com.example.jusikreport.marketcap.repository;

import com.example.jusikreport.marketcap.domain.MarketCap;
import com.example.jusikreport.marketcap.dto.MarketCapSearchCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MarketCapRepositoryTest {

    @Autowired
    private MarketCapRepository marketCapRepository;

    @Test
    @DisplayName("N/A(null) 값은 오름차순/내림차순 정렬 모두에서 무조건 가장 마지막에 배치된다")
    void testNullsLastSorting() {
        marketCapRepository.deleteAll();

        MarketCap item1 = new MarketCap("005930", "삼성전자", "KOSPI", "400조", 400000000000000L, 1,
                70000L, "70,000", 1000L, "+1,000", 1.45, "+1.45%", 100L, "100",
                5000000000L, "50억", 55.0, "55%", 10000000L, "10,000,000", 15.0, "15.00", 10.0, "10.00", 1.2, "1.20", 33.2, "33.20%");

        MarketCap item2 = new MarketCap("000660", "SK하이닉스", "KOSPI", "100조", 100000000000000L, 2,
                150000L, "150,000", -2000L, "-2,000", -1.3, "-1.3%", 500L, "500",
                700000000L, "7억", 50.0, "50%", 5000000L, "5,000,000", null, "N/A", null, "N/A", null, "N/A", null, "N/A");

        MarketCap item3 = new MarketCap("035420", "NAVER", "KOSPI", "30조", 30000000000000L, 3,
                200000L, "200,000", 0L, "0", 0.0, "0.0%", 500L, "500",
                160000000L, "1.6억", 45.0, "45%", 2000000L, "2,000,000", 8.0, "8.00", 12.0, "12.00", 0.9, "0.90", 15.0, "15.00%");

        marketCapRepository.saveAll(List.of(item1, item2, item3));

        MarketCapSearchCondition condition = new MarketCapSearchCondition(null, null);

        // 1. PER 오름차순 (ASC) -> 8.0, 15.0, null (N/A는 맨 뒤)
        Page<MarketCap> ascResult = marketCapRepository.findAll(
                MarketCapSpecification.from(condition, "per", Sort.Direction.ASC),
                PageRequest.of(0, 10)
        );
        List<Double> ascPers = ascResult.getContent().stream().map(MarketCap::getPer).toList();
        assertThat(ascPers).containsExactly(8.0, 15.0, null);

        // 2. PER 내림차순 (DESC) -> 15.0, 8.0, null (N/A는 맨 뒤)
        Page<MarketCap> descResult = marketCapRepository.findAll(
                MarketCapSpecification.from(condition, "per", Sort.Direction.DESC),
                PageRequest.of(0, 10)
        );
        List<Double> descPers = descResult.getContent().stream().map(MarketCap::getPer).toList();
        assertThat(descPers).containsExactly(15.0, 8.0, null);

        // 3. PBR 오름차순 (ASC) -> 0.9, 1.2, null (N/A는 맨 뒤)
        Page<MarketCap> ascPbrResult = marketCapRepository.findAll(
                MarketCapSpecification.from(condition, "pbr", Sort.Direction.ASC),
                PageRequest.of(0, 10)
        );
        List<Double> ascPbrs = ascPbrResult.getContent().stream().map(MarketCap::getPbr).toList();
        assertThat(ascPbrs).containsExactly(0.9, 1.2, null);

        // 4. 영업이익증가율 오름차순 (ASC) -> 15.0, 33.2, null (N/A는 맨 뒤)
        Page<MarketCap> ascGrowthResult = marketCapRepository.findAll(
                MarketCapSpecification.from(condition, "operatingProfitGrowthRate", Sort.Direction.ASC),
                PageRequest.of(0, 10)
        );
        List<Double> ascGrowths = ascGrowthResult.getContent().stream().map(MarketCap::getOperatingProfitGrowthRate).toList();
        assertThat(ascGrowths).containsExactly(15.0, 33.2, null);
    }

    @Test
    @DisplayName("시가총액 최소/최대 조건으로 조회 시 범위 내 종목만 필터링된다")
    void testMarketCapRangeFilter() {
        marketCapRepository.deleteAll();

        MarketCap item1 = new MarketCap("005930", "삼성전자", "KOSPI", "4,000,000", 4000000L, 1);
        MarketCap item2 = new MarketCap("000660", "SK하이닉스", "KOSPI", "1,000,000", 1000000L, 2);
        MarketCap item3 = new MarketCap("035420", "NAVER", "KOSPI", "300,000", 300000L, 3);

        marketCapRepository.saveAll(List.of(item1, item2, item3));

        MarketCapSearchCondition condition = new MarketCapSearchCondition(null, null, 500000L, 3500000L);
        Page<MarketCap> filtered = marketCapRepository.findAll(
                MarketCapSpecification.from(condition),
                PageRequest.of(0, 10)
        );

        List<String> names = filtered.getContent().stream().map(MarketCap::getStockName).toList();
        assertThat(names).containsExactly("SK하이닉스");
    }
}
