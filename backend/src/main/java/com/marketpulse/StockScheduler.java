package com.marketpulse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketpulse.dto.SentimentRequest;
import com.marketpulse.dto.SentimentResponse;
import com.marketpulse.model.NewsArticle;
import com.marketpulse.model.StockPrice;
import com.marketpulse.model.Alert;
import com.marketpulse.repository.NewsArticleRepository;
import com.marketpulse.repository.StockPriceRepository;
import com.marketpulse.repository.AlertRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class StockScheduler {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final StockPriceRepository repository;
    private final NewsArticleRepository newsRepository;
    private final AlertRepository alertRepository;

    @Value("${newsapi.api.key}")
    private String newsApiKey;

    @Value("${twelvedata.api.key}")
    private String priceApiKey;

    @Value("${sentiment.api.url}")
    private String sentimentApiUrl;

    public StockScheduler(StockPriceRepository repository, NewsArticleRepository newsRepository, AlertRepository alertRepository) {
        this.repository = repository;
        this.newsRepository = newsRepository;
        this.alertRepository = alertRepository;
    }

    public void fetchStockPrices() {
        String[] tickers = {"TSLA", "AAPL", "MSFT"};

        for (String ticker : tickers) {
            try {
                String url = "https://api.twelvedata.com/price?symbol=" + ticker + "&apikey=" + priceApiKey;
                String rawJson = restTemplate.getForObject(url, String.class);
                System.out.println("Raw response for " + ticker + ": " + rawJson);

                StockPriceResponse response = objectMapper.readValue(rawJson, StockPriceResponse.class);

                if (response == null || response.getPrice() == null) {
                    System.err.println("No price data returned for " + ticker);
                    continue;
                }

                double parsedPrice;
                try {
                    parsedPrice = Double.parseDouble(response.getPrice().trim());
                } catch (NumberFormatException nfe) {
                    System.err.println("Invalid price format for " + ticker + ": " + response.getPrice());
                    continue;
                }

                StockPrice stockPrice = new StockPrice(ticker, parsedPrice, LocalDateTime.now());
                repository.save(stockPrice);
                System.out.println("Saved: " + ticker + " $" + parsedPrice);

                List<StockPrice> latestTwo = repository.findTop2ByTickerIgnoreCaseOrderByTimestampDesc(ticker);
                if (latestTwo.size() == 2) {
                    double prev = latestTwo.get(1).getPrice();
                    double curr = latestTwo.get(0).getPrice();
                    double change = ((curr - prev) / prev) * 100;
                    System.out.printf("%s change: %.2f%%\n", ticker, change);
                }

                Thread.sleep(1000); // Avoid rate limiting

            } catch (Exception e) {
                System.err.println("Error fetching price for " + ticker + ": " + e.getMessage());
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void fetchNewsAndAnalyzeSentiment() {
        String[] tickers = {"TSLA", "AAPL", "MSFT"};

        for (String ticker : tickers) {
            try {
                String newsUrl = "https://newsapi.org/v2/everything?q=" + ticker +
                        "&sortBy=publishedAt&pageSize=5&apiKey=" + newsApiKey;

                Map<String, Object> newsResponse = restTemplate.getForObject(newsUrl, Map.class);
                List<Map<String, Object>> articles = (List<Map<String, Object>>) newsResponse.get("articles");

                List<String> headlines = new ArrayList<>();
                for (Map<String, Object> article : articles) {
                    headlines.add((String) article.get("title"));
                }

                SentimentRequest sentimentRequest = new SentimentRequest(headlines);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<SentimentRequest> requestEntity = new HttpEntity<>(sentimentRequest, headers);

                ResponseEntity<SentimentResponse> response = restTemplate.exchange(
                        sentimentApiUrl,
                        HttpMethod.POST,
                        requestEntity,
                        SentimentResponse.class
                );

                SentimentResponse sentimentData = response.getBody();

                for (int i = 0; i < headlines.size(); i++) {
                    NewsArticle article = new NewsArticle(
                            ticker,
                            headlines.get(i),
                            "NewsAPI",
                            sentimentData.getIndividual_scores().get(i),
                            LocalDateTime.now()
                    );
                    newsRepository.save(article);
                    System.out.println("Saved headline: " + article.getHeadline());
                }

            } catch (Exception e) {
                System.err.println("Error fetching or processing news for " + ticker + ": " + e.getMessage());
            }
        }
    }

    public void checkAndTriggerAlerts() {
        String[] tickers = {"TSLA", "AAPL", "MSFT"};

        for (String ticker : tickers) {
            List<StockPrice> prices = repository.findTop2ByTickerIgnoreCaseOrderByTimestampDesc(ticker);
            List<NewsArticle> news = newsRepository.findTop5ByTickerIgnoreCaseOrderByTimestampDesc(ticker);

            if (prices.size() < 2 || news.isEmpty()) continue;

            double prev = prices.get(1).getPrice();
            double curr = prices.get(0).getPrice();
            double priceDrop = ((prev - curr) / prev) * 100;

            double avgSentiment = news.stream()
                    .mapToDouble(NewsArticle::getSentimentScore)
                    .average()
                    .orElse(0.0);

            if (priceDrop >= 2.0 && avgSentiment < -0.2) {
                Alert alert = new Alert();
                alert.setTicker(ticker);
                alert.setPriceDropPct(priceDrop);
                alert.setSentimentThreshold(avgSentiment);
                alert.setTriggeredAt(LocalDateTime.now());
                alertRepository.save(alert);

                System.out.printf("⚠️ ALERT triggered for %s: %.2f%% drop with sentiment %.2f\n", ticker, priceDrop, avgSentiment);
            }
        }
    }

    @Scheduled(fixedRate = 1800000) // every 30 minutes
    public void runAllScheduledTasks() {
        fetchStockPrices();
        fetchNewsAndAnalyzeSentiment();
        checkAndTriggerAlerts();
    }

    @PostConstruct
    public void testStocksNow() {
        System.out.println("Running all tasks on startup...");
        fetchStockPrices();
        fetchNewsAndAnalyzeSentiment();
        checkAndTriggerAlerts();
    }
}
