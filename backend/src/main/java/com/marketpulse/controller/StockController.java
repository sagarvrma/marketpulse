package com.marketpulse.controller;

import com.marketpulse.model.StockPrice;
import com.marketpulse.repository.StockPriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockPriceRepository repository;

    @Autowired
    public StockController(StockPriceRepository repository) {
        this.repository = repository;
    }

    // Return latest 10 entries for a given ticker
    @GetMapping("/{ticker}/history")
    public List<StockPrice> getStockHistory(@PathVariable String ticker) {
        return repository.findTop10ByTickerIgnoreCaseOrderByTimestampDesc(ticker);
    }

    // Return only the latest price for a given ticker
    @GetMapping("/{ticker}/latest")
    public ResponseEntity<StockPrice> getLatestStockPrice(@PathVariable String ticker) {
        Optional<StockPrice> latest = repository.findTopByTickerIgnoreCaseOrderByTimestampDesc(ticker);
        return latest.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
