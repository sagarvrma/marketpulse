package com.marketpulse.repository;

import com.marketpulse.model.NewsArticle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {
    List<NewsArticle> findByTickerIgnoreCaseOrderByTimestampDesc(String ticker);
    List<NewsArticle> findTop5ByTickerIgnoreCaseOrderByTimestampDesc(String ticker);
}
