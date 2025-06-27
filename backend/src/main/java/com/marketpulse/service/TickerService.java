package com.marketpulse.service;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TickerService {
    private static final String TICKER_FILE = "tickers.txt";
    private final List<String> tickers = new ArrayList<>();

    @PostConstruct
    public void loadTickers() throws IOException {
        Path path = Paths.get(TICKER_FILE);
        List<String> lines;
        if (Files.exists(path)) {
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        } else {
            InputStream is = getClass().getClassLoader().getResourceAsStream(TICKER_FILE);
            if (is == null) {
                throw new IOException("tickers.txt not found");
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                lines = reader.lines().toList();
            }
            Files.write(path, lines, StandardCharsets.UTF_8);
        }

        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty()) {
                tickers.add(line.toUpperCase());
            }
        }
    }

    public List<String> getTickers() {
        return Collections.unmodifiableList(tickers);
    }

    public synchronized void addTicker(String ticker) {
        ticker = ticker.toUpperCase();
        if (!tickers.contains(ticker)) {
            tickers.add(ticker);
            saveTickers();
        }
    }

    public synchronized void removeTicker(String ticker) {
        if (tickers.remove(ticker.toUpperCase())) {
            saveTickers();
        }
    }

    private void saveTickers() {
        try {
            Files.write(Paths.get(TICKER_FILE), tickers, StandardCharsets.UTF_8);
        } catch (IOException e) {
            // log error in real application
            e.printStackTrace();
        }
    }
}
