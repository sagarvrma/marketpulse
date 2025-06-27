package com.marketpulse.dto;

import java.util.List;

public class SentimentResponse {
    private double average_score;
    private List<Double> individual_scores;

    public SentimentResponse() {}

    public double getAverage_score() {
        return average_score;
    }

    public List<Double> getIndividual_scores() {
        return individual_scores;
    }
}
