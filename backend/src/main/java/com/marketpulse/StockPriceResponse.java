package com.marketpulse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class StockPriceResponse {
    private String price;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}