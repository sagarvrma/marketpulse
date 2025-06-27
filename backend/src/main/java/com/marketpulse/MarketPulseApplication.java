package com.marketpulse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MarketPulseApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MarketPulseApplication.class, args);

        // Manually trigger alerts right after startup (for testing)
        StockScheduler scheduler = context.getBean(StockScheduler.class);
        scheduler.checkAndTriggerAlerts();
    }
}
