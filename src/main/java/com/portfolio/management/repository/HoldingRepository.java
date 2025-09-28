package com.portfolio.management.repository;

import com.portfolio.management.entity.Holding;
import com.portfolio.management.enums.HoldingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Holding entity
 * Provides data access methods for portfolio holding operations
 */
@Repository
public interface HoldingRepository extends JpaRepository<Holding, String>, JpaSpecificationExecutor<Holding> {

    // Basic Queries

    /**
     * Find holdings by portfolio ID
     */
    List<Holding> findByPortfolioId(String portfolioId);

    /**
     * Find holdings by portfolio ID with pagination
     */
    Page<Holding> findByPortfolioId(String portfolioId, Pageable pageable);

    /**
     * Find holdings by portfolio ID and status
     */
    List<Holding> findByPortfolioIdAndStatus(String portfolioId, HoldingStatus status);

    /**
     * Find holdings by portfolio ID and status with pagination
     */
    Page<Holding> findByPortfolioIdAndStatus(String portfolioId, HoldingStatus status, Pageable pageable);

    /**
     * Find holdings by symbol
     */
    List<Holding> findBySymbol(String symbol);

    /**
     * Find holdings by symbol and portfolio user ID (for current user)
     */
    List<Holding> findBySymbolAndPortfolioUserId(String symbol, String userId);

    /**
     * Find holdings by status
     */
    List<Holding> findByStatus(HoldingStatus status);

    /**
     * Find holding by portfolio ID and symbol
     */
    Optional<Holding> findByPortfolioIdAndSymbol(String portfolioId, String symbol);

    /**
     * Find holdings by sector
     */
    List<Holding> findBySector(String sector);

    /**
     * Find holdings by country
     */
    List<Holding> findByCountry(String country);

    /**
     * Find holdings by portfolio ID and sector
     */
    List<Holding> findByPortfolioIdAndSector(String portfolioId, String sector);

    /**
     * Find holdings by portfolio ID and country
     */
    List<Holding> findByPortfolioIdAndCountry(String portfolioId, String country);

    /**
     * Find holdings created after a specific date
     */
    List<Holding> findByCreatedAtAfter(Instant date);

    /**
     * Find holdings updated before a specific date (for stale data)
     */
    List<Holding> findByUpdatedAtBeforeAndStatus(Instant date, HoldingStatus status);

    /**
     * Find holdings updated after a specific date
     */
    List<Holding> findByUpdatedAtAfter(Instant date);

    // Existence Checks

    /**
     * Check if holding exists by ID and portfolio ID
     */
    boolean existsByIdAndPortfolioId(String holdingId, String portfolioId);

    /**
     * Check if holding exists by portfolio ID and symbol
     */
    boolean existsByPortfolioIdAndSymbol(String portfolioId, String symbol);

    /**
     * Check if holdings exist for portfolio
     */
    boolean existsByPortfolioId(String portfolioId);

    // Count Queries

    /**
     * Count holdings by portfolio ID
     */
    int countByPortfolioId(String portfolioId);

    /**
     * Count holdings by portfolio ID and status
     */
    int countByPortfolioIdAndStatus(String portfolioId, HoldingStatus status);

    /**
     * Count holdings by status
     */
    long countByStatus(HoldingStatus status);

    /**
     * Count holdings by user ID and status
     */
    long countByPortfolioUserIdAndStatus(String userId, HoldingStatus status);

    /**
     * Count holdings by symbol
     */
    long countBySymbol(String symbol);

    // Financial Calculations

    /**
     * Calculate total current value of holdings in a portfolio
     */
    @Query("SELECT COALESCE(SUM(h.quantity * h.currentPrice), 0) " +
            "FROM Holding h WHERE h.portfolio.id = :portfolioId AND h.status = :status")
    Optional<BigDecimal> calculateTotalValue(@Param("portfolioId") String portfolioId,
                                             @Param("status") HoldingStatus status);

    /**
     * Calculate total invested amount in a portfolio
     */
    @Query("SELECT COALESCE(SUM(h.quantity * h.averagePrice), 0) " +
            "FROM Holding h WHERE h.portfolio.id = :portfolioId AND h.status = :status")
    Optional<BigDecimal> calculateTotalInvested(@Param("portfolioId") String portfolioId,
                                                @Param("status") HoldingStatus status);

    /**
     * Calculate total unrealized gain/loss for a portfolio
     */
    @Query("SELECT COALESCE(SUM(h.quantity * (h.currentPrice - h.averagePrice)), 0) " +
            "FROM Holding h WHERE h.portfolio.id = :portfolioId AND h.status = :status")
    Optional<BigDecimal> calculateTotalGainLoss(@Param("portfolioId") String portfolioId,
                                                @Param("status") HoldingStatus status);

    /**
     * Calculate day change for a portfolio
     */
    @Query("SELECT COALESCE(SUM(h.quantity * (h.currentPrice - h.previousClosePrice)), 0) " +
            "FROM Holding h WHERE h.portfolio.id = :portfolioId AND h.status = :status " +
            "AND h.previousClosePrice IS NOT NULL")
    Optional<BigDecimal> calculateDayChange(@Param("portfolioId") String portfolioId,
                                            @Param("status") HoldingStatus status);

    /**
     * Calculate holding performance over time period
     */
    @Query("SELECT COALESCE(" +
            "  (h.quantity * h.currentPrice) - " +
            "  (SELECT COALESCE(SUM(t.quantity * t.price), 0) FROM Transaction t " +
            "   WHERE t.holding.id = :holdingId AND t.type = 'BUY' " +
            "   AND t.transactionDate BETWEEN :startDate AND :endDate), 0) " +
            "FROM Holding h WHERE h.id = :holdingId")
    Optional<BigDecimal> calculateHoldingPerformance(@Param("holdingId") String holdingId,
                                                     @Param("startDate") Instant startDate,
                                                     @Param("endDate") Instant endDate);

    // Analysis Queries

    /**
     * Find top holdings by current value in a portfolio
     */
    @Query("SELECT h FROM Holding h WHERE h.portfolio.id = :portfolioId AND h.status = :status " +
            "ORDER BY (h.quantity * h.currentPrice) DESC")
    List<Holding> findTopHoldingsByValue(@Param("portfolioId") String portfolioId,
                                         @Param("status") HoldingStatus status,
                                         Pageable pageable);

    /**
     * Find top holdings by gain/loss in a portfolio
     */
    @Query("SELECT h FROM Holding h WHERE h.portfolio.id = :portfolioId AND h.status = :status " +
            "ORDER BY (h.quantity * (h.currentPrice - h.averagePrice)) DESC")
    List<Holding> findTopHoldingsByGainLoss(@Param("portfolioId") String portfolioId,
                                            @Param("status") HoldingStatus status,
                                            Pageable pageable);

    /**
     * Find worst performing holdings in a portfolio
     */
    @Query("SELECT h FROM Holding h WHERE h.portfolio.id = :portfolioId AND h.status = :status " +
            "ORDER BY (h.quantity * (h.currentPrice - h.averagePrice)) ASC")
    List<Holding> findWorstPerformingHoldings(@Param("portfolioId") String portfolioId,
                                              @Param("status") HoldingStatus status,
                                              Pageable pageable);

    /**
     * Find holdings with significant day change (above threshold)
     */
    @Query("SELECT h FROM Holding h WHERE h.portfolio.id = :portfolioId AND h.status = :status " +
            "AND h.previousClosePrice IS NOT NULL " +
            "AND ABS((h.currentPrice - h.previousClosePrice) / h.previousClosePrice * 100) >= :changeThreshold")
    List<Holding> findHoldingsWithSignificantDayChange(@Param("portfolioId") String portfolioId,
                                                       @Param("status") HoldingStatus status,
                                                       @Param("changeThreshold") BigDecimal changeThreshold);

    /**
     * Find holdings above target price
     */
    @Query("SELECT h FROM Holding h WHERE h.portfolio.id = :portfolioId AND h.status = :status " +
            "AND h.targetPrice IS NOT NULL AND h.currentPrice >= h.targetPrice")
    List<Holding> findHoldingsAboveTargetPrice(@Param("portfolioId") String portfolioId,
                                               @Param("status") HoldingStatus status);

    /**
     * Find holdings below stop loss
     */
    @Query("SELECT h FROM Holding h WHERE h.portfolio.id = :portfolioId AND h.status = :status " +
            "AND h.stopLossPrice IS NOT NULL AND h.currentPrice <= h.stopLossPrice")
    List<Holding> findHoldingsBelowStopLoss(@Param("portfolioId") String portfolioId,
                                            @Param("status") HoldingStatus status);

    /**
     * Find overconcentrated holdings (above percentage threshold of portfolio value)
     */
    @Query("SELECT h FROM Holding h WHERE h.portfolio.id = :portfolioId AND h.status = :status " +
            "AND (h.quantity * h.currentPrice) > " +
            "(SELECT SUM(h2.quantity * h2.currentPrice) * :concentrationLimit / 100 " +
            " FROM Holding h2 WHERE h2.portfolio.id = :portfolioId AND h2.status = :status)")
    List<Holding> findOverconcentratedHoldings(@Param("portfolioId") String portfolioId,
                                               @Param("status") HoldingStatus status,
                                               @Param("concentrationLimit") BigDecimal concentrationLimit);

    // Sector and Geographic Analysis

    /**
     * Get sector allocation data for a portfolio
     */
    @Query("SELECT h.sector as sector, " +
            "COUNT(h) as holdingCount, " +
            "SUM(h.quantity * h.currentPrice) as totalValue, " +
            "AVG(h.quantity * (h.currentPrice - h.averagePrice)) as avgGainLoss " +
            "FROM Holding h WHERE h.portfolio.id = :portfolioId AND h.status = :status " +
            "GROUP BY h.sector")
    List<Object[]> getSectorAllocationData(@Param("portfolioId") String portfolioId,
                                           @Param("status") HoldingStatus status);

    /**
     * Get geographic allocation data for a portfolio
     */
    @Query("SELECT h.country as country, " +
            "COUNT(h) as holdingCount, " +
            "SUM(h.quantity * h.currentPrice) as totalValue, " +
            "AVG(h.quantity * (h.currentPrice - h.averagePrice)) as avgGainLoss " +
            "FROM Holding h WHERE h.portfolio.id = :portfolioId AND h.status = :status " +
            "GROUP BY h.country")
    List<Object[]> getGeographicAllocationData(@Param("portfolioId") String portfolioId,
                                               @Param("status") HoldingStatus status);

    /**
     * Get holdings by sector in a portfolio
     */
    @Query("SELECT h FROM Holding h WHERE h.portfolio.id = :portfolioId AND h.status = :status " +
            "AND h.sector = :sector ORDER BY (h.quantity * h.currentPrice) DESC")
    List<Holding> findBySectorInPortfolio(@Param("portfolioId") String portfolioId,
                                          @Param("status") HoldingStatus status,
                                          @Param("sector") String sector);

    /**
     * Get holdings by country in a portfolio
     */
    @Query("SELECT h FROM Holding h WHERE h.portfolio.id = :portfolioId AND h.status = :status " +
            "AND h.country = :country ORDER BY (h.quantity * h.currentPrice) DESC")
    List<Holding> findByCountryInPortfolio(@Param("portfolioId") String portfolioId,
                                           @Param("status") HoldingStatus status,
                                           @Param("country") String country);

    // Search Queries

    /**
     * Search holdings by symbol or company name
     */
    @Query("SELECT h FROM Holding h WHERE h.portfolio.id = :portfolioId " +
            "AND (LOWER(h.symbol) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(h.companyName) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Holding> searchHoldings(@Param("portfolioId") String portfolioId,
                                 @Param("searchTerm") String searchTerm);

    /**
     * Find holdings by symbol containing (case-insensitive)
     */
    List<Holding> findBySymbolContainingIgnoreCase(String symbol);

    /**
     * Find holdings by company name containing (case-insensitive)
     */
    List<Holding> findByCompanyNameContainingIgnoreCase(String companyName);

    // Date Range Queries

    /**
     * Find holdings created between dates
     */
    List<Holding> findByCreatedAtBetween(Instant startDate, Instant endDate);

    /**
     * Find holdings updated between dates
     */
    List<Holding> findByUpdatedAtBetween(Instant startDate, Instant endDate);

    /**
     * Find holdings with last dividend date between dates
     */
    List<Holding> findByLastDividendDateBetween(Instant startDate, Instant endDate);

    // Price and Target Queries

    /**
     * Find holdings with current price between range
     */
    @Query("SELECT h FROM Holding h WHERE h.portfolio.id = :portfolioId AND h.status = :status " +
            "AND h.currentPrice BETWEEN :minPrice AND :maxPrice")
    List<Holding> findByPriceRange(@Param("portfolioId") String portfolioId,
                                   @Param("status") HoldingStatus status,
                                   @Param("minPrice") BigDecimal minPrice,
                                   @Param("maxPrice") BigDecimal maxPrice);

    /**
     * Find holdings with value between range
     */
    @Query("SELECT h FROM Holding h WHERE h.portfolio.id = :portfolioId AND h.status = :status " +
            "AND (h.quantity * h.currentPrice) BETWEEN :minValue AND :maxValue")
    List<Holding> findByValueRange(@Param("portfolioId") String portfolioId,
                                   @Param("status") HoldingStatus status,
                                   @Param("minValue") BigDecimal minValue,
                                   @Param("maxValue") BigDecimal maxValue);

    /**
     * Find holdings with target price set
     */
    List<Holding> findByTargetPriceIsNotNullAndPortfolioIdAndStatus(String portfolioId, HoldingStatus status);

    /**
     * Find holdings with stop loss price set
     */
    List<Holding> findByStopLossPriceIsNotNullAndPortfolioIdAndStatus(String portfolioId, HoldingStatus status);

    // Quantity Queries

    /**
     * Find holdings with zero quantity
     */
    @Query("SELECT h FROM Holding h WHERE h.portfolio.id = :portfolioId " +
            "AND h.quantity = 0")
    List<Holding> findZeroQuantityHoldings(@Param("portfolioId") String portfolioId);

    /**
     * Find holdings with quantity below threshold
     */
    @Query("SELECT h FROM Holding h WHERE h.portfolio.id = :portfolioId AND h.status = :status " +
            "AND h.quantity < :threshold")
    List<Holding> findLowQuantityHoldings(@Param("portfolioId") String portfolioId,
                                          @Param("status") HoldingStatus status,
                                          @Param("threshold") BigDecimal threshold);

    // Performance Queries

    /**
     * Find top gaining holdings across all user portfolios
     */
    @Query("SELECT h FROM Holding h WHERE h.portfolio.user.id = :userId AND h.status = :status " +
            "ORDER BY (h.quantity * (h.currentPrice - h.averagePrice)) DESC")
    List<Holding> findTopGainingHoldingsForUser(@Param("userId") String userId,
                                                @Param("status") HoldingStatus status,
                                                Pageable pageable);

    /**
     * Find worst performing holdings across all user portfolios
     */
    @Query("SELECT h FROM Holding h WHERE h.portfolio.user.id = :userId AND h.status = :status " +
            "ORDER BY (h.quantity * (h.currentPrice - h.averagePrice)) ASC")
    List<Holding> findWorstPerformingHoldingsForUser(@Param("userId") String userId,
                                                     @Param("status") HoldingStatus status,
                                                     Pageable pageable);

    /**
     * Find holdings with highest day change
     */
    @Query("SELECT h FROM Holding h WHERE h.portfolio.user.id = :userId AND h.status = :status " +
            "AND h.previousClosePrice IS NOT NULL " +
            "ORDER BY (h.quantity * (h.currentPrice - h.previousClosePrice)) DESC")
    List<Holding> findHoldingsWithHighestDayChange(@Param("userId") String userId,
                                                   @Param("status") HoldingStatus status,
                                                   Pageable pageable);

    // Bulk Operations

    /**
     * Update status for multiple holdings
     */
    @Query("UPDATE Holding h SET h.status = :status, h.updatedAt = :updatedAt " +
            "WHERE h.id IN :holdingIds")
    int updateStatusForHoldings(@Param("holdingIds") List<String> holdingIds,
                                @Param("status") HoldingStatus status,
                                @Param("updatedAt") Instant updatedAt);

    /**
     * Update current price for holdings by symbol
     */
    @Query("UPDATE Holding h SET h.previousClosePrice = h.currentPrice, " +
            "h.currentPrice = :newPrice, h.updatedAt = :updatedAt " +
            "WHERE h.symbol = :symbol AND h.status = :status")
    int updatePriceBySymbol(@Param("symbol") String symbol,
                            @Param("newPrice") BigDecimal newPrice,
                            @Param("status") HoldingStatus status,
                            @Param("updatedAt") Instant updatedAt);

    // Statistics

    /**
     * Get holding statistics for a user
     */
    @Query("SELECT " +
            "COUNT(h) as totalHoldings, " +
            "COUNT(CASE WHEN h.status = 'ACTIVE' THEN 1 END) as activeHoldings, " +
            "COUNT(DISTINCT h.symbol) as uniqueSymbols, " +
            "COUNT(DISTINCT h.sector) as uniqueSectors, " +
            "COUNT(DISTINCT h.country) as uniqueCountries, " +
            "AVG(h.quantity * h.currentPrice) as avgHoldingValue, " +
            "SUM(h.quantity * h.currentPrice) as totalValue, " +
            "SUM(h.quantity * h.averagePrice) as totalInvested " +
            "FROM Holding h WHERE h.portfolio.user.id = :userId")
    Object[] getHoldingStatistics(@Param("userId") String userId);

    /**
     * Get most held symbols across all user portfolios
     */
    @Query("SELECT h.symbol, COUNT(h) as holdingCount, SUM(h.quantity) as totalQuantity " +
            "FROM Holding h WHERE h.portfolio.user.id = :userId AND h.status = :status " +
            "GROUP BY h.symbol ORDER BY COUNT(h) DESC")
    List<Object[]> getMostHeldSymbols(@Param("userId") String userId,
                                      @Param("status") HoldingStatus status,
                                      Pageable pageable);

    /**
     * Get sector distribution for user
     */
    @Query("SELECT h.sector, COUNT(h) as holdingCount, SUM(h.quantity * h.currentPrice) as totalValue " +
            "FROM Holding h WHERE h.portfolio.user.id = :userId AND h.status = :status " +
            "GROUP BY h.sector ORDER BY SUM(h.quantity * h.currentPrice) DESC")
    List<Object[]> getSectorDistributionForUser(@Param("userId") String userId,
                                                @Param("status") HoldingStatus status);

    /**
     * Find holdings that haven't been updated recently
     */
    @Query("SELECT h FROM Holding h WHERE h.status = :status " +
            "AND h.updatedAt < :staleThreshold " +
            "ORDER BY h.updatedAt ASC")
    List<Holding> findStaleHoldings(@Param("status") HoldingStatus status,
                                    @Param("staleThreshold") Instant staleThreshold);

    /**
     * Find duplicate holdings (same symbol in same portfolio)
     */
    @Query("SELECT h.symbol, h.portfolio.id, COUNT(h) as duplicateCount " +
            "FROM Holding h WHERE h.status = :status " +
            "GROUP BY h.symbol, h.portfolio.id " +
            "HAVING COUNT(h) > 1")
    List<Object[]> findDuplicateHoldings(@Param("status") HoldingStatus status);

    // Custom Queries for Reporting

    /**
     * Get portfolio diversification metrics
     */
    @Query("SELECT " +
            "COUNT(DISTINCT h.sector) as sectorCount, " +
            "COUNT(DISTINCT h.country) as countryCount, " +
            "COUNT(h) as totalHoldings, " +
            "MAX(h.quantity * h.currentPrice) / SUM(h.quantity * h.currentPrice) * 100 as maxConcentration " +
            "FROM Holding h WHERE h.portfolio.id = :portfolioId AND h.status = :status")
    Object[] getPortfolioDiversificationMetrics(@Param("portfolioId") String portfolioId,
                                                @Param("status") HoldingStatus status);

    /**
     * Get holdings requiring attention (multiple criteria)
     */
    @Query("SELECT h FROM Holding h WHERE h.portfolio.id = :portfolioId AND h.status = :status " +
            "AND (h.targetPrice IS NOT NULL AND h.currentPrice >= h.targetPrice " +
            "OR h.stopLossPrice IS NOT NULL AND h.currentPrice <= h.stopLossPrice " +
            "OR h.previousClosePrice IS NOT NULL AND " +
            "   ABS((h.currentPrice - h.previousClosePrice) / h.previousClosePrice * 100) >= 5)")
    List<Holding> findHoldingsRequiringAttention(@Param("portfolioId") String portfolioId,
                                                 @Param("status") HoldingStatus status);

    // Cleanup Queries

    /**
     * Find orphaned holdings (no portfolio or inactive portfolio)
     */
    @Query("SELECT h FROM Holding h WHERE h.portfolio IS NULL " +
            "OR h.portfolio.isActive = false")
    List<Holding> findOrphanedHoldings();

    /**
     * Find holdings with invalid data (negative quantity, zero/negative prices)
     */
    @Query("SELECT h FROM Holding h WHERE h.quantity < 0 " +
            "OR h.currentPrice <= 0 OR h.averagePrice <= 0 " +
            "OR (h.targetPrice IS NOT NULL AND h.targetPrice <= 0) " +
            "OR (h.stopLossPrice IS NOT NULL AND h.stopLossPrice <= 0)")
    List<Holding> findHoldingsWithInvalidData();

    /**
     * Find holdings with missing essential data
     */
    @Query("SELECT h FROM Holding h WHERE h.symbol IS NULL OR h.symbol = '' " +
            "OR h.quantity IS NULL OR h.currentPrice IS NULL OR h.averagePrice IS NULL")
    List<Holding> findHoldingsWithMissingData();
}