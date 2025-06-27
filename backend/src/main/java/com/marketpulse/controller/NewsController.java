package com.marketpulse.controller;

import com.marketpulse.model.NewsArticle;
import com.marketpulse.repository.NewsArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsArticleRepository repository;

    @Autowired
    public NewsController(NewsArticleRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{ticker}")
    public List<NewsArticle> getNewsByTicker(@PathVariable String ticker) {
        return repository.findByTickerIgnoreCaseOrderByTimestampDesc(ticker);
    }
}
