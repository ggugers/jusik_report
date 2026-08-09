package com.example.jusikreport.marketcap.repository;

import com.example.jusikreport.marketcap.domain.MarketCap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MarketCapRepository extends JpaRepository<MarketCap, Long>, JpaSpecificationExecutor<MarketCap> {

    Optional<MarketCap> findByStockCode(String stockCode);

    Optional<MarketCap> findByStockName(String stockName);

    List<MarketCap> findByStockCodeIn(Collection<String> stockCodes);

    List<MarketCap> findByStockNameIn(Collection<String> stockNames);
}
