package com.marketpulse.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class NewsArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ticker;
    private String headline;
    private String source;
    private double sentimentScore;
    private LocalDateTime timestamp;

    public NewsArticle() {
    }

    public NewsArticle(String ticker, String headline, String source, double sentimentScore, LocalDateTime timestamp) {
        this.ticker = ticker;
        this.headline = headline;
        this.source = source;
        this.sentimentScore = sentimentScore;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public String getTicker() {
        return ticker;
    }

    public String getHeadline() {
        return headline;
    }

    public String getSource() {
        return source;
    }

    public double getSentimentScore() {
        return sentimentScore;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
