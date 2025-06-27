package com.marketpulse.repository;

import com.marketpulse.model.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {
    Optional<StockPrice> findTopByTickerIgnoreCaseOrderByTimestampDesc(String ticker);
    List<StockPrice> findByTickerIgnoreCaseOrderByTimestampDesc(String ticker);
    List<StockPrice> findTop2ByTickerIgnoreCaseOrderByTimestampDesc(String ticker);
    List<StockPrice> findTop10ByTickerIgnoreCaseOrderByTimestampDesc(String ticker); // ← Add this
}
