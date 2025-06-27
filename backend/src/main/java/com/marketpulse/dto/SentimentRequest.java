package com.marketpulse.dto;

import java.util.List;

public class SentimentRequest {
    private List<String> headlines;

    public SentimentRequest() {}

    public SentimentRequest(List<String> headlines) {
        this.headlines = headlines;
    }

    public List<String> getHeadlines() {
        return headlines;
    }

    public void setHeadlines(List<String> headlines) {
        this.headlines = headlines;
    }
}
