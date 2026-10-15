package com.example.jusikreport.marketcap.service;

import com.example.jusikreport.marketcap.crawler.NaverMarketCapCrawler;
import com.example.jusikreport.marketcap.domain.MarketCap;
import com.example.jusikreport.marketcap.dto.CrawledMarketCap;
import com.example.jusikreport.marketcap.dto.SyncResult;
import com.example.jusikreport.marketcap.repository.MarketCapRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MarketCapSyncServiceImpl implements MarketCapSyncService {

    private static final int MAX_PAGES = 100;

    private final NaverMarketCapCrawler naverMarketCapCrawler;
    private final MarketCapRepository marketCapRepository;

    public MarketCapSyncServiceImpl(NaverMarketCapCrawler naverMarketCapCrawler, MarketCapRepository marketCapRepository) {
        this.naverMarketCapCrawler = naverMarketCapCrawler;
        this.marketCapRepository = marketCapRepository;
    }

    @Override
    public SyncResult sync() {
        SyncAccumulator accumulator = new SyncAccumulator();
        syncMarket("KOSPI", null, accumulator);
        syncMarket("KOSDAQ", 1, accumulator);
        return new SyncResult(accumulator.savedCount, accumulator.updatedCount, accumulator.failedCount, accumulator.visitedPages);
    }

    private void syncMarket(String marketType, Integer sosok, SyncAccumulator accumulator) {
        for (int page = 1; page <= MAX_PAGES; page++) {
            List<CrawledMarketCap> crawledItems = naverMarketCapCrawler.crawlPage(page, sosok, marketType);
            if (crawledItems.isEmpty()) {
                break;
            }

            accumulator.visitedPages++;

            for (CrawledMarketCap item : crawledItems) {
                try {
                    MarketCap marketCap = marketCapRepository.findByStockCode(item.stockCode())
                            .orElseGet(() -> marketCapRepository.findByStockName(item.stockName()).orElse(null));

                    if (marketCap == null) {
                        marketCapRepository.save(new MarketCap(
                                item.stockCode(),
                                item.stockName(),
                                item.marketType(),
                                item.marketCapText(),
                                item.marketCapValue(),
                                item.rankNo(),
                                item.currentPrice(),
                                item.currentPriceText(),
                                item.priceChange(),
                                item.priceChangeText(),
                                item.changeRate(),
                                item.changeRateText(),
                                item.faceValue(),
                                item.faceValueText(),
                                item.listedShares(),
                                item.listedSharesText(),
                                item.foreignRatio(),
                                item.foreignRatioText(),
                                item.volume(),
                                item.volumeText(),
                                item.per(),
                                item.perText(),
                                item.roe(),
                                item.roeText(),
                                item.pbr(),
                                item.pbrText(),
                                item.operatingProfitGrowthRate(),
                                item.operatingProfitGrowthRateText()
                        ));
                        accumulator.savedCount++;
                        continue;
                    }

                    marketCap.update(
                            item.stockCode(),
                            item.stockName(),
                            item.marketType(),
                            item.marketCapText(),
                            item.marketCapValue(),
                            item.rankNo(),
                            item.currentPrice(),
                            item.currentPriceText(),
                            item.priceChange(),
                            item.priceChangeText(),
                            item.changeRate(),
                            item.changeRateText(),
                            item.faceValue(),
                            item.faceValueText(),
                            item.listedShares(),
                            item.listedSharesText(),
                            item.foreignRatio(),
                            item.foreignRatioText(),
                            item.volume(),
                            item.volumeText(),
                            item.per(),
                            item.perText(),
                            item.roe(),
                            item.roeText(),
                            item.pbr(),
                            item.pbrText(),
                            item.operatingProfitGrowthRate(),
                            item.operatingProfitGrowthRateText()
                    );
                    accumulator.updatedCount++;
                } catch (Exception exception) {
                    accumulator.failedCount++;
                }
            }
        }
    }

    private static final class SyncAccumulator {
        private int savedCount;
        private int updatedCount;
        private int failedCount;
        private int visitedPages;
    }
}
