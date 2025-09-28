package com.portfolio.management.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Service interface for market data operations
 * Provides real-time and historical market data
 */
public interface MarketDataService {

    /**
     * Get current price for a symbol
     */
    BigDecimal getCurrentPrice(String symbol);

    /**
     * Get current prices for multiple symbols
     */
    Map<String, BigDecimal> getCurrentPrices(List<String> symbols);

    /**
     * Get company information for a symbol
     */
    Map<String, String> getCompanyInfo(String symbol);

    /**
     * Get historical prices for a symbol
     */
    List<Map<String, Object>> getHistoricalPrices(String symbol, Instant startDate, Instant endDate);

    /**
     * Get market status (open/closed)
     */
    boolean isMarketOpen();

    /**
     * Get market hours information
     */
    Map<String, Object> getMarketHours();

    /**
     * Search for symbols by company name or symbol
     */
    List<Map<String, Object>> searchSymbols(String query);

    /**
     * Validate if symbol exists and is tradable
     */
    boolean isValidSymbol(String symbol);

    /**
     * Get dividend information for a symbol
     */
    List<Map<String, Object>> getDividendHistory(String symbol, Instant startDate, Instant endDate);

    /**
     * Get stock split information for a symbol
     */
    List<Map<String, Object>> getStockSplits(String symbol, Instant startDate, Instant endDate);
}