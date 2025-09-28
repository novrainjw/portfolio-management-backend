package com.portfolio.management.service;

import com.portfolio.management.dto.request.HoldingCreateRequest;
import com.portfolio.management.dto.request.HoldingUpdateRequest;
import com.portfolio.management.dto.response.HoldingResponse;
import com.portfolio.management.dto.response.HoldingSummaryResponse;
import com.portfolio.management.entity.Holding;
import com.portfolio.management.enums.HoldingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Service interface for managing portfolio holdings
 * Provides operations for CRUD operations, calculations, and analysis
 */
public interface HoldingService {

    // CRUD Operations

    /**
     * Add a new holding to a portfolio
     */
    HoldingResponse addHolding(String portfolioId, HoldingCreateRequest request);

    /**
     * Update an existing holding
     */
    HoldingResponse updateHolding(String holdingId, HoldingUpdateRequest request);

    /**
     * Get holding by ID
     */
    HoldingResponse getHoldingById(String holdingId);

    /**
     * Get all holdings for a portfolio
     */
    List<HoldingResponse> getPortfolioHoldings(String portfolioId);

    /**
     * Get holdings for a portfolio with pagination
     */
    Page<HoldingResponse> getPortfolioHoldings(String portfolioId, Pageable pageable);

    /**
     * Get holdings by symbol across all user portfolios
     */
    List<HoldingResponse> getHoldingsBySymbol(String symbol);

    /**
     * Get holdings by status
     */
    List<HoldingResponse> getHoldingsByStatus(String portfolioId, HoldingStatus status);

    /**
     * Delete/remove a holding
     */
    void deleteHolding(String holdingId);

    /**
     * Soft delete - mark as inactive
     */
    void deactivateHolding(String holdingId);

    /**
     * Reactivate a deactivated holding
     */
    void reactivateHolding(String holdingId);

    // Bulk Operations

    /**
     * Update multiple holdings status
     */
    void updateMultipleHoldingStatuses(List<String> holdingIds, HoldingStatus status);

    /**
     * Delete multiple holdings
     */
    void deleteMultipleHoldings(List<String> holdingIds);

    // Price and Value Operations

    /**
     * Update current price for a holding
     */
    HoldingResponse updateHoldingPrice(String holdingId, BigDecimal newPrice);

    /**
     * Update current prices for all holdings in a portfolio
     */
    void updatePortfolioHoldingPrices(String portfolioId);

    /**
     * Update current prices for all holdings of a specific symbol
     */
    void updateHoldingPricesBySymbol(String symbol, BigDecimal newPrice);

    /**
     * Refresh all holding prices using market data
     */
    void refreshAllHoldingPrices();

    // Financial Calculations

    /**
     * Calculate total value of a holding (quantity * current price)
     */
    BigDecimal calculateHoldingValue(String holdingId);

    /**
     * Calculate total invested amount (quantity * average price)
     */
    BigDecimal calculateTotalInvested(String holdingId);

    /**
     * Calculate unrealized gain/loss for a holding
     */
    BigDecimal calculateUnrealizedGainLoss(String holdingId);

    /**
     * Calculate unrealized gain/loss percentage
     */
    BigDecimal calculateUnrealizedGainLossPercentage(String holdingId);

    /**
     * Calculate day change for a holding
     */
    BigDecimal calculateDayChange(String holdingId);

    /**
     * Calculate day change percentage
     */
    BigDecimal calculateDayChangePercentage(String holdingId);

    /**
     * Calculate portfolio percentage for a holding
     */
    BigDecimal calculatePortfolioPercentage(String holdingId);

    /**
     * Recalculate and update all financial metrics for a holding
     */
    HoldingResponse recalculateHoldingMetrics(String holdingId);

    // Transaction Integration

    /**
     * Update holding after a buy transaction
     */
    HoldingResponse processBuyTransaction(String portfolioId, String symbol,
                                          BigDecimal quantity, BigDecimal price,
                                          BigDecimal fees, Instant transactionDate);

    /**
     * Update holding after a sell transaction
     */
    HoldingResponse processSellTransaction(String holdingId, BigDecimal quantity,
                                           BigDecimal price, BigDecimal fees,
                                           Instant transactionDate);

    /**
     * Process dividend for a holding
     */
    HoldingResponse processDividend(String holdingId, BigDecimal dividendPerShare,
                                    Instant exDividendDate, Instant paymentDate);

    /**
     * Process stock split for a holding
     */
    HoldingResponse processStockSplit(String holdingId, BigDecimal splitRatio);

    /**
     * Update average price after transaction
     */
    void updateAveragePrice(String holdingId, BigDecimal newQuantity,
                            BigDecimal transactionPrice, BigDecimal transactionQuantity);

    // Portfolio Analysis

    /**
     * Get top holdings by value in a portfolio
     */
    List<HoldingResponse> getTopHoldingsByValue(String portfolioId, int limit);

    /**
     * Get top holdings by gain/loss in a portfolio
     */
    List<HoldingResponse> getTopHoldingsByGainLoss(String portfolioId, int limit);

    /**
     * Get worst performing holdings in a portfolio
     */
    List<HoldingResponse> getWorstPerformingHoldings(String portfolioId, int limit);

    /**
     * Get holdings summary for portfolio
     */
    List<HoldingSummaryResponse> getHoldingsSummary(String portfolioId);

    // Risk Management

    /**
     * Get holdings that are above target price
     */
    List<HoldingResponse> getHoldingsAboveTargetPrice(String portfolioId);

    /**
     * Get holdings that are below stop loss
     */
    List<HoldingResponse> getHoldingsBelowStopLoss(String portfolioId);

    /**
     * Get holdings requiring attention (various alert conditions)
     */
    List<HoldingResponse> getHoldingsRequiringAttention(String portfolioId);

    /**
     * Check if holding exceeds concentration limit
     */
    boolean isHoldingOverConcentrated(String holdingId, BigDecimal concentrationLimit);

    /**
     * Get overconcentrated holdings in portfolio
     */
    List<HoldingResponse> getOverconcentratedHoldings(String portfolioId, BigDecimal concentrationLimit);

    // Sector and Geographic Analysis

    /**
     * Get holdings grouped by sector
     */
    Map<String, List<HoldingResponse>> getHoldingsBySector(String portfolioId);

    /**
     * Get holdings grouped by country
     */
    Map<String, List<HoldingResponse>> getHoldingsByCountry(String portfolioId);

    /**
     * Get sector allocation by value
     */
    Map<String, BigDecimal> getSectorAllocation(String portfolioId);

    /**
     * Get geographic allocation by value
     */
    Map<String, BigDecimal> getGeographicAllocation(String portfolioId);

    // Search and Filtering

    /**
     * Search holdings by symbol or company name
     */
    List<HoldingResponse> searchHoldings(String portfolioId, String searchTerm);

    /**
     * Get holdings with filters
     */
    Page<HoldingResponse> getHoldingsWithFilters(String portfolioId, HoldingStatus status,
                                                 String sector, String country,
                                                 BigDecimal minValue, BigDecimal maxValue,
                                                 Instant createdAfter, Instant updatedAfter,
                                                 Pageable pageable);

    // Performance Tracking

    /**
     * Get holding performance over time period
     */
    BigDecimal getHoldingPerformance(String holdingId, Instant startDate, Instant endDate);

    /**
     * Get holding price history (if available)
     */
    List<Map<String, Object>> getHoldingPriceHistory(String holdingId,
                                                     Instant startDate, Instant endDate);

    // Validation and Utilities

    /**
     * Validate holding access for current user
     */
    void validateHoldingAccess(String holdingId);

    /**
     * Check if holding exists
     */
    boolean holdingExists(String holdingId);

    /**
     * Check if holding belongs to portfolio
     */
    boolean isHoldingInPortfolio(String holdingId, String portfolioId);

    /**
     * Get holding entity by ID (for internal use)
     */
    Holding getHoldingEntityById(String holdingId);

    /**
     * Check if symbol already exists in portfolio
     */
    boolean symbolExistsInPortfolio(String portfolioId, String symbol);

    /**
     * Get existing holding by symbol in portfolio
     */
    HoldingResponse getHoldingBySymbolInPortfolio(String portfolioId, String symbol);

    // Synchronization

    /**
     * Sync holding with broker data
     */
    void syncHoldingWithBroker(String holdingId, String brokerId);

    /**
     * Sync all portfolio holdings with broker
     */
    void syncPortfolioHoldingsWithBroker(String portfolioId, String brokerId);

    // Market Data Integration

    /**
     * Update holding with latest market data
     */
    HoldingResponse updateHoldingWithMarketData(String holdingId);

    /**
     * Schedule price updates for all active holdings
     */
    void scheduleHoldingPriceUpdates();

    /**
     * Get holdings that haven't been updated recently
     */
    List<HoldingResponse> getStaleHoldings(Instant staleThreshold);

    // Statistics

    /**
     * Get holding statistics for portfolio
     */
    Map<String, Object> getHoldingStatistics(String portfolioId);

    /**
     * Get total holdings count for user
     */
    long getUserTotalHoldingsCount(String userId);

    /**
     * Get active holdings count for portfolio
     */
    int getActiveHoldingsCount(String portfolioId);

    // Import/Export

    /**
     * Export holdings data
     */
    byte[] exportHoldingsData(String portfolioId, String format);

    /**
     * Import holdings from file
     */
    List<HoldingResponse> importHoldingsData(String portfolioId, byte[] fileData, String format);
}