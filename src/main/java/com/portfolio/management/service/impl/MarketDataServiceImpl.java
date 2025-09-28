package com.portfolio.management.service.impl;

import com.portfolio.management.exception.MarketDataException;
import com.portfolio.management.service.MarketDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Market Data Service Implementation
 * Provides integration with external market data providers
 * Note: This is a simplified implementation. In production, you would integrate
 * with services like Alpha Vantage, IEX Cloud, Yahoo Finance, etc.
 */
@Service
public class MarketDataServiceImpl implements MarketDataService {

    private static final Logger logger = LoggerFactory.getLogger(MarketDataServiceImpl.class);

    private final RestTemplate restTemplate;

    @Value("${market.data.api.key:demo_key}")
    private String apiKey;

    @Value("${market.data.api.base.url:https://api.example.com}")
    private String baseUrl;

    @Value("${market.data.cache.duration.seconds:300}")
    private int cacheDurationSeconds;

    // Mock data for demonstration - in production, remove this
    private final Map<String, BigDecimal> mockPrices = new HashMap<>();
    private final Map<String, Map<String, String>> mockCompanyInfo = new HashMap<>();

    public MarketDataServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        initializeMockData();
    }

    @Override
    public BigDecimal getCurrentPrice(String symbol) {
        return null;
    }

    @Override
    public Map<String, BigDecimal> getCurrentPrices(List<String> symbols) {
        return Map.of();
    }

    @Override
    @Cacheable(value = "company_info", key = "#symbol")
    public Map<String, String> getCompanyInfo(String symbol) {
        logger.debug("Fetching company info for symbol: {}", symbol);

        try {
            String upperSymbol = symbol.toUpperCase();

            // Return mock data if available
            if (mockCompanyInfo.containsKey(upperSymbol)) {
                return new HashMap<>(mockCompanyInfo.get(upperSymbol));
            }

            // Fallback API call simulation
            return fetchCompanyInfoFromApi(symbol);

        } catch (Exception e) {
            logger.error("Failed to fetch company info for symbol {}: {}", symbol, e.getMessage());

            // Return minimal info as fallback
            Map<String, String> fallbackInfo = new HashMap<>();
            fallbackInfo.put("symbol", symbol.toUpperCase());
            fallbackInfo.put("name", symbol.toUpperCase() + " Corp");
            fallbackInfo.put("sector", "Unknown");
            fallbackInfo.put("country", "US");

            return fallbackInfo;
        }
    }

    @Override
    public List<Map<String, Object>> getHistoricalPrices(String symbol, Instant startDate, Instant endDate) {
        logger.debug("Fetching historical prices for symbol: {} from {} to {}", symbol, startDate, endDate);

        try {
            // In production, this would make an API call to get historical data
            return fetchHistoricalPricesFromApi(symbol, startDate, endDate);

        } catch (Exception e) {
            logger.error("Failed to fetch historical prices for symbol {}: {}", symbol, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    @Cacheable(value = "market_status", unless = "#result == null")
    public boolean isMarketOpen() {
        try {
            // Simple market hours check (9:30 AM - 4:00 PM EST on weekdays)
            LocalTime now = LocalTime.now(ZoneId.of("America/New_York"));
            LocalTime marketOpen = LocalTime.of(9, 30);
            LocalTime marketClose = LocalTime.of(16, 0);

            // Check if it's a weekday
            int dayOfWeek = java.time.LocalDate.now(ZoneId.of("America/New_York")).getDayOfWeek().getValue();
            boolean isWeekday = dayOfWeek >= 1 && dayOfWeek <= 5;

            return isWeekday && now.isAfter(marketOpen) && now.isBefore(marketClose);

        } catch (Exception e) {
            logger.error("Failed to determine market status: {}", e.getMessage());
            return false; // Assume market is closed if we can't determine status
        }
    }

    @Override
    @Cacheable(value = "market_hours")
    public Map<String, Object> getMarketHours() {
        Map<String, Object> marketHours = new HashMap<>();

        try {
            marketHours.put("timezone", "America/New_York");
            marketHours.put("regularOpen", "09:30");
            marketHours.put("regularClose", "16:00");
            marketHours.put("extendedOpen", "04:00");
            marketHours.put("extendedClose", "20:00");
            marketHours.put("isOpen", isMarketOpen());
            marketHours.put("dayOfWeek", java.time.LocalDate.now(ZoneId.of("America/New_York")).getDayOfWeek().toString());

            return marketHours;

        } catch (Exception e) {
            logger.error("Failed to get market hours: {}", e.getMessage());
            return marketHours; // Return partial data
        }
    }

    @Override
    @Cacheable(value = "symbol_search", key = "#query")
    public List<Map<String, Object>> searchSymbols(String query) {
        logger.debug("Searching symbols with query: {}", query);

        try {
            // In production, this would search through a symbols database or API
            return searchSymbolsFromApi(query);

        } catch (Exception e) {
            logger.error("Failed to search symbols with query {}: {}", query, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    @Cacheable(value = "symbol_validation", key = "#symbol")
    public boolean isValidSymbol(String symbol) {
        logger.debug("Validating symbol: {}", symbol);

        try {
            // Check if we can get a current price for the symbol
            BigDecimal price = getCurrentPrice(symbol);
            return price != null && price.compareTo(BigDecimal.ZERO) > 0;

        } catch (Exception e) {
            logger.debug("Symbol {} validation failed: {}", symbol, e.getMessage());
            return false;
        }
    }

    @Override
    public List<Map<String, Object>> getDividendHistory(String symbol, Instant startDate, Instant endDate) {
        logger.debug("Fetching dividend history for symbol: {} from {} to {}", symbol, startDate, endDate);

        try {
            // In production, this would fetch from dividend data API
            return fetchDividendHistoryFromApi(symbol, startDate, endDate);

        } catch (Exception e) {
            logger.error("Failed to fetch dividend history for symbol {}: {}", symbol, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<Map<String, Object>> getStockSplits(String symbol, Instant startDate, Instant endDate) {
        logger.debug("Fetching stock splits for symbol: {} from {} to {}", symbol, startDate, endDate);

        try {
            // In production, this would fetch from stock splits API
            return fetchStockSplitsFromApi(symbol, startDate, endDate);

        } catch (Exception e) {
            logger.error("Failed to fetch stock splits for symbol {}: {}", symbol, e.getMessage());
            return Collections.emptyList();
        }
    }

    // Private helper methods

    private BigDecimal fetchPriceFromApi(String symbol) {
        try {
            // Simulate API call with mock data
            String upperSymbol = symbol.toUpperCase();

            // Return mock price with some variation if available
            if (mockPrices.containsKey(upperSymbol)) {
                BigDecimal basePrice = mockPrices.get(upperSymbol);
                BigDecimal variation = basePrice.multiply(BigDecimal.valueOf((Math.random() - 0.5) * 0.02));
                return basePrice.add(variation).setScale(2, BigDecimal.ROUND_HALF_UP);
            }

            // For unknown symbols, return a random price
            double randomPrice = 50 + (Math.random() * 200); // Random price between $50-$250
            return BigDecimal.valueOf(randomPrice).setScale(2, BigDecimal.ROUND_HALF_UP);

            /* In production, this would be:
            String url = baseUrl + "/quote?symbol=" + symbol + "&apikey=" + apiKey;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> data = response.getBody();
                Double price = (Double) data.get("price");
                return price != null ? BigDecimal.valueOf(price) : null;
            }
            */

        } catch (Exception e) {
            logger.error("API call failed for symbol {}: {}", symbol, e.getMessage());
            throw new MarketDataException("Failed to fetch price from API", e);
        }
    }

    private Map<String, String> fetchCompanyInfoFromApi(String symbol) {
        try {
            // Simulate API call for company info
            Map<String, String> info = new HashMap<>();
            info.put("symbol", symbol.toUpperCase());
            info.put("name", symbol.toUpperCase() + " Corporation");
            info.put("sector", getRandomSector());
            info.put("country", "US");
            info.put("currency", "USD");
            info.put("exchange", "NASDAQ");

            return info;

        } catch (Exception e) {
            logger.error("Failed to fetch company info from API for symbol {}: {}", symbol, e.getMessage());
            throw new MarketDataException("Failed to fetch company info from API", e);
        }
    }

    private List<Map<String, Object>> fetchHistoricalPricesFromApi(String symbol, Instant startDate, Instant endDate) {
        try {
            // Simulate historical price data
            List<Map<String, Object>> historicalData = new ArrayList<>();
            BigDecimal currentPrice = getCurrentPrice(symbol);

            // Generate mock historical data (daily prices going back)
            long days = java.time.Duration.between(startDate, endDate).toDays();
            for (int i = 0; i < Math.min(days, 30); i++) { // Limit to 30 days for demo
                Map<String, Object> dayData = new HashMap<>();
                dayData.put("date", startDate.plus(java.time.Duration.ofDays(i)));
                dayData.put("open", currentPrice.multiply(BigDecimal.valueOf(0.95 + Math.random() * 0.1)));
                dayData.put("high", currentPrice.multiply(BigDecimal.valueOf(1.0 + Math.random() * 0.05)));
                dayData.put("low", currentPrice.multiply(BigDecimal.valueOf(0.95 + Math.random() * 0.05)));
                dayData.put("close", currentPrice.multiply(BigDecimal.valueOf(0.98 + Math.random() * 0.04)));
                dayData.put("volume", (long) (1000000 + Math.random() * 5000000));

                historicalData.add(dayData);
            }

            return historicalData;

        } catch (Exception e) {
            logger.error("Failed to fetch historical prices from API: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<Map<String, Object>> searchSymbolsFromApi(String query) {
        try {
            List<Map<String, Object>> results = new ArrayList<>();

            // Simple mock search - in production, this would search a symbols database
            String upperQuery = query.toUpperCase();

            for (String symbol : mockPrices.keySet()) {
                if (symbol.contains(upperQuery)) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("symbol", symbol);
                    result.put("name", symbol + " Corporation");
                    result.put("type", "stock");
                    result.put("exchange", "NASDAQ");

                    results.add(result);

                    if (results.size() >= 10) break; // Limit results
                }
            }

            return results;

        } catch (Exception e) {
            logger.error("Failed to search symbols: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<Map<String, Object>> fetchDividendHistoryFromApi(String symbol, Instant startDate, Instant endDate) {
        try {
            // Mock dividend data
            List<Map<String, Object>> dividends = new ArrayList<>();

            // Simulate quarterly dividends
            BigDecimal quarterlyDividend = BigDecimal.valueOf(0.50 + Math.random() * 2.0);

            long months = java.time.temporal.ChronoUnit.MONTHS.between(
                    startDate.atZone(ZoneId.systemDefault()).toLocalDate(),
                    endDate.atZone(ZoneId.systemDefault()).toLocalDate()
            );

            for (int i = 0; i < months / 3; i++) { // Quarterly dividends
                Map<String, Object> dividend = new HashMap<>();
                dividend.put("exDate", startDate.plus(java.time.Duration.ofDays(i * 90)));
                dividend.put("payDate", startDate.plus(java.time.Duration.ofDays(i * 90 + 14)));
                dividend.put("amount", quarterlyDividend);
                dividend.put("type", "regular");

                dividends.add(dividend);
            }

            return dividends;

        } catch (Exception e) {
            logger.error("Failed to fetch dividend history: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<Map<String, Object>> fetchStockSplitsFromApi(String symbol, Instant startDate, Instant endDate) {
        try {
            // Mock stock split data (rare events)
            List<Map<String, Object>> splits = new ArrayList<>();

            // Simulate occasional stock splits
            if (Math.random() < 0.1) { // 10% chance of a split in the period
                Map<String, Object> split = new HashMap<>();
                split.put("date", startDate.plus(java.time.Duration.ofDays((long) (Math.random() * 365))));
                split.put("ratio", "2:1"); // 2-for-1 split
                split.put("factor", 2.0);

                splits.add(split);
            }

            return splits;

        } catch (Exception e) {
            logger.error("Failed to fetch stock splits: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private String getRandomSector() {
        String[] sectors = {
                "Technology", "Healthcare", "Financial Services", "Consumer Cyclical",
                "Consumer Defensive", "Industrials", "Energy", "Real Estate",
                "Materials", "Utilities", "Communication Services"
        };

        return sectors[(int) (Math.random() * sectors.length)];
    }

    private void initializeMockData() {
        // Initialize some mock stock prices for testing
        mockPrices.put("AAPL", BigDecimal.valueOf(175.50));
        mockPrices.put("GOOGL", BigDecimal.valueOf(2650.75));
        mockPrices.put("MSFT", BigDecimal.valueOf(335.20));
        mockPrices.put("AMZN", BigDecimal.valueOf(3200.00));
        mockPrices.put("TSLA", BigDecimal.valueOf(850.25));
        mockPrices.put("META", BigDecimal.valueOf(310.80));
        mockPrices.put("NFLX", BigDecimal.valueOf(425.60));
        mockPrices.put("NVDA", BigDecimal.valueOf(780.90));
        mockPrices.put("AMD", BigDecimal.valueOf(125.40));
        mockPrices.put("INTC", BigDecimal.valueOf(55.75));

        // Initialize mock company info
        mockCompanyInfo.put("AAPL", createCompanyInfo("AAPL", "Apple Inc.", "Technology", "US"));
        mockCompanyInfo.put("GOOGL", createCompanyInfo("GOOGL", "Alphabet Inc.", "Communication Services", "US"));
        mockCompanyInfo.put("MSFT", createCompanyInfo("MSFT", "Microsoft Corporation", "Technology", "US"));
        mockCompanyInfo.put("AMZN", createCompanyInfo("AMZN", "Amazon.com Inc.", "Consumer Cyclical", "US"));
        mockCompanyInfo.put("TSLA", createCompanyInfo("TSLA", "Tesla Inc.", "Consumer Cyclical", "US"));
        mockCompanyInfo.put("META", createCompanyInfo("META", "Meta Platforms Inc.", "Communication Services", "US"));
        mockCompanyInfo.put("NFLX", createCompanyInfo("NFLX", "Netflix Inc.", "Communication Services", "US"));
        mockCompanyInfo.put("NVDA", createCompanyInfo("NVDA", "NVIDIA Corporation", "Technology", "US"));
        mockCompanyInfo.put("AMD", createCompanyInfo("AMD", "Advanced Micro Devices Inc.", "Technology", "US"));
        mockCompanyInfo.put("INTC", createCompanyInfo("INTC", "Intel Corporation", "Technology", "US"));
    }

    private Map<String, String> createCompanyInfo(String symbol, String name, String sector, String country) {
        Map<String, String> info = new HashMap<>();
        info.put("symbol", symbol);
        info.put("name", name);
        info.put("sector", sector);
        info.put("country", country);
        info.put("currency", "USD");
        info.put("exchange", "NASDAQ");
        return info;
    }
}