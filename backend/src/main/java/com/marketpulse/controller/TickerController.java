package com.marketpulse.controller;

import com.marketpulse.service.TickerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickers")
public class TickerController {
    private final TickerService tickerService;

    @Autowired
    public TickerController(TickerService tickerService) {
        this.tickerService = tickerService;
    }

    @GetMapping
    public List<String> getTickers() {
        return tickerService.getTickers();
    }

    @PostMapping
    public ResponseEntity<Void> addTicker(@RequestBody String ticker) {
        tickerService.addTicker(ticker);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{ticker}")
    public ResponseEntity<Void> removeTicker(@PathVariable String ticker) {
        tickerService.removeTicker(ticker);
        return ResponseEntity.ok().build();
    }
}
