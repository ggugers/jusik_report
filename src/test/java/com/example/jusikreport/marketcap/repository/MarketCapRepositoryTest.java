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
                5000000000L, "50억", 55.0, "55%", 10000000L, "10,000,000", 15.0, "15.00", 10.0, "10.00");

        MarketCap item2 = new MarketCap("000660", "SK하이닉스", "KOSPI", "100조", 100000000000000L, 2,
                150000L, "150,000", -2000L, "-2,000", -1.3, "-1.3%", 500L, "500",
                700000000L, "7억", 50.0, "50%", 5000000L, "5,000,000", null, "N/A", null, "N/A");

        MarketCap item3 = new MarketCap("035420", "NAVER", "KOSPI", "30조", 30000000000000L, 3,
                200000L, "200,000", 0L, "0", 0.0, "0.0%", 500L, "500",
                160000000L, "1.6억", 45.0, "45%", 2000000L, "2,000,000", 8.0, "8.00", 12.0, "12.00");

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
    }
}
