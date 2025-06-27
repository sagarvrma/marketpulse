package com.marketpulse.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ticker;
    private double priceDropPct;
    private double sentimentThreshold;
    private LocalDateTime triggeredAt;

    public Alert() {}

    public Alert(String ticker, double priceDropPct, double sentimentThreshold, LocalDateTime triggeredAt) {
        this.ticker = ticker;
        this.priceDropPct = priceDropPct;
        this.sentimentThreshold = sentimentThreshold;
        this.triggeredAt = triggeredAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }
    public double getPriceDropPct() { return priceDropPct; }
    public void setPriceDropPct(double priceDropPct) { this.priceDropPct = priceDropPct; }
    public double getSentimentThreshold() { return sentimentThreshold; }
    public void setSentimentThreshold(double sentimentThreshold) { this.sentimentThreshold = sentimentThreshold; }
    public LocalDateTime getTriggeredAt() { return triggeredAt; }
    public void setTriggeredAt(LocalDateTime triggeredAt) { this.triggeredAt = triggeredAt; }
}
