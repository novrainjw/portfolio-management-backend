package com.portfolio.management.repository;

import com.portfolio.management.entity.Portfolio;
import com.portfolio.management.enums.PortfolioStatus;
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
import java.util.Map;
import java.util.Optional;

/**
 * Repository interface for Portfolio entity
 * Provides data access methods for portfolio operations
 */
@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, String>, JpaSpecificationExecutor<Portfolio> {

    // Basic Queries

    /**
     * Find portfolios by user ID
     */
    List<Portfolio> findByUserId(String userId);

    /**
     * Find portfolios by user ID with pagination
     */
    Page<Portfolio> findByUserId(String userId, Pageable pageable);

    /**
     * Find portfolios by user ID and status
     */
    List<Portfolio> findByUserIdAndStatus(String userId, PortfolioStatus status);

    /**
     * Find portfolios by user ID and status with pagination
     */
    Page<Portfolio> findByUserIdAndStatus(String userId, PortfolioStatus status, Pageable pageable);

    /**
     * Find portfolios by status
     */
    List<Portfolio> findByStatus(PortfolioStatus status);

    /**
     * Find portfolios by currency
     */
    List<Portfolio> findByCurrency(String currency);

    /**
     * Find portfolios by user ID and currency
     */
    List<Portfolio> findByUserIdAndCurrency(String userId, String currency);

    /**
     * Find portfolios created after a specific date
     */
    List<Portfolio> findByCreatedAtAfter(Instant date);

    /**
     * Find portfolios updated after a specific date
     */
    List<Portfolio> findByUpdatedAtAfter(Instant date);

    // Existence Checks

    /**
     * Check if portfolio exists by user ID and name
     */
    boolean existsByUserIdAndName(String userId, String name);

    /**
     * Check if portfolio exists by user ID and name, excluding a specific portfolio ID
     */
    boolean existsByUserIdAndNameAndIdNot(String userId, String name, String excludeId);

    /**
     * Check if portfolio exists by ID and user ID
     */
    boolean existsByIdAndUserId(String portfolioId, String userId);

    // Count Queries

    /**
     * Count portfolios by user ID
     */
    long countByUserId(String userId);

    /**
     * Count portfolios by status
     */
    long countByStatus(PortfolioStatus status);

    /**
     * Count portfolios by user ID and status
     */
    long countByUserIdAndStatus(String userId, PortfolioStatus status);

    /**
     * Count active holdings in a portfolio
     */
    @Query("SELECT COUNT(h) FROM Holding h WHERE h.portfolio.id = :portfolioId AND h.status = 'ACTIVE'")
    int countActiveHoldings(@Param("portfolioId") String portfolioId);

    /**
     * Count total transactions in a portfolio
     */
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.portfolio.id = :portfolioId")
    int countTransactions(@Param("portfolioId") String portfolioId);

    // Financial Calculations

    /**
     * Calculate total current value of all holdings in a portfolio
     */
    @Query("SELECT COALESCE(SUM(h.quantity * h.currentPrice), 0) " +
            "FROM Holding h WHERE h.portfolio.id = :portfolioId AND h.status = 'ACTIVE'")
    Optional<BigDecimal> calculateTotalValue(@Param("portfolioId") String portfolioId);

    /**
     * Calculate total invested amount in a portfolio
     */
    @Query("SELECT COALESCE(SUM(h.quantity * h.averagePrice), 0) " +
            "FROM Holding h WHERE h.portfolio.id = :portfolioId AND h.status = 'ACTIVE'")
    Optional<BigDecimal> calculateTotalInvested(@Param("portfolioId") String portfolioId);

    /**
     * Calculate day change for a portfolio
     */
    @Query("SELECT COALESCE(SUM(h.quantity * (h.currentPrice - h.previousClosePrice)), 0) " +
            "FROM Holding h WHERE h.portfolio.id = :portfolioId AND h.status = 'ACTIVE'")
    Optional<BigDecimal> calculateDayChange(@Param("portfolioId") String portfolioId);

    /**
     * Calculate total dividends for a portfolio
     */
    @Query("SELECT COALESCE(SUM(t.price * t.quantity), 0) " +
            "FROM Transaction t WHERE t.portfolio.id = :portfolioId AND t.type = 'DIVIDEND'")
    Optional<BigDecimal> calculateTotalDividends(@Param("portfolioId") String portfolioId);

    /**
     * Calculate portfolio performance over time period
     */
    @Query("SELECT COALESCE(" +
            "  (SELECT SUM(h.quantity * h.currentPrice) FROM Holding h WHERE h.portfolio.id = :portfolioId AND h.status = 'ACTIVE') - " +
            "  (SELECT SUM(t.price * t.quantity + COALESCE(t.fees, 0)) FROM Transaction t " +
            "   WHERE t.portfolio.id = :portfolioId AND t.type = 'BUY' AND t.transactionDate BETWEEN :startDate AND :endDate) + " +
            "  (SELECT SUM(t.price * t.quantity - COALESCE(t.fees, 0)) FROM Transaction t " +
            "   WHERE t.portfolio.id = :portfolioId AND t.type = 'SELL' AND t.transactionDate BETWEEN :startDate AND :endDate), 0)")
    Optional<BigDecimal> calculatePerformance(@Param("portfolioId") String portfolioId,
                                              @Param("startDate") Instant startDate,
                                              @Param("endDate") Instant endDate);

    // Portfolio Analysis

    /**
     * Get sector allocation for a portfolio
     */
    @Query("SELECT h.sector as sector, SUM(h.quantity * h.currentPrice) as value " +
            "FROM Holding h WHERE h.portfolio.id = :portfolioId AND h.status = 'ACTIVE' " +
            "GROUP BY h.sector")
    List<Object[]> getSectorAllocationData(@Param("portfolioId") String portfolioId);

    /**
     * Calculate sector allocation as percentages
     */
    default Map<String, BigDecimal> calculateSectorAllocation(String portfolioId) {
        List<Object[]> data = getSectorAllocationData(portfolioId);
        BigDecimal totalValue = calculateTotalValue(portfolioId).orElse(BigDecimal.ZERO);

        Map<String, BigDecimal> allocation = new java.util.HashMap<>();
        if (totalValue.compareTo(BigDecimal.ZERO) > 0) {
            for (Object[] row : data) {
                String sector = (String) row[0];
                BigDecimal value = (BigDecimal) row[1];
                BigDecimal percentage = value.divide(totalValue, 4, java.math.RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                allocation.put(sector != null ? sector : "Unknown", percentage);
            }
        }
        return allocation;
    }

    /**
     * Get geographic allocation for a portfolio
     */
    @Query("SELECT h.country as country, SUM(h.quantity * h.currentPrice) as value " +
            "FROM Holding h WHERE h.portfolio.id = :portfolioId AND h.status = 'ACTIVE' " +
            "GROUP BY h.country")
    List<Object[]> getGeographicAllocationData(@Param("portfolioId") String portfolioId);

    /**
     * Calculate geographic allocation as percentages
     */
    default Map<String, BigDecimal> calculateGeographicAllocation(String portfolioId) {
        List<Object[]> data = getGeographicAllocationData(portfolioId);
        BigDecimal totalValue = calculateTotalValue(portfolioId).orElse(BigDecimal.ZERO);

        Map<String, BigDecimal> allocation = new java.util.HashMap<>();
        if (totalValue.compareTo(BigDecimal.ZERO) > 0) {
            for (Object[] row : data) {
                String country = (String) row[0];
                BigDecimal value = (BigDecimal) row[1];
                BigDecimal percentage = value.divide(totalValue, 4, java.math.RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                allocation.put(country != null ? country : "Unknown", percentage);
            }
        }
        return allocation;
    }

    /**
     * Get asset type allocation (assuming we have asset type in holdings)
     */
    @Query("SELECT 'STOCK' as assetType, SUM(h.quantity * h.currentPrice) as value " +
            "FROM Holding h WHERE h.portfolio.id = :portfolioId AND h.status = 'ACTIVE'")
    List<Object[]> getAssetTypeAllocationData(@Param("portfolioId") String portfolioId);

    /**
     * Calculate asset type allocation as percentages
     */
    default Map<String, BigDecimal> calculateAssetTypeAllocation(String portfolioId) {
        List<Object[]> data = getAssetTypeAllocationData(portfolioId);
        BigDecimal totalValue = calculateTotalValue(portfolioId).orElse(BigDecimal.ZERO);

        Map<String, BigDecimal> allocation = new java.util.HashMap<>();
        if (totalValue.compareTo(BigDecimal.ZERO) > 0) {
            for (Object[] row : data) {
                String assetType = (String) row[0];
                BigDecimal value = (BigDecimal) row[1];
                BigDecimal percentage = value.divide(totalValue, 4, java.math.RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                allocation.put(assetType, percentage);
            }
        }
        return allocation;
    }

    /**
     * Find top holdings by value in a portfolio
     */
    @Query("SELECT h.symbol FROM Holding h WHERE h.portfolio.id = :portfolioId AND h.status = 'ACTIVE' " +
            "ORDER BY (h.quantity * h.currentPrice) DESC")
    List<String> findTopHoldingSymbolsByValue(@Param("portfolioId") String portfolioId, Pageable pageable);

    /**
     * Find top holdings by value with limit
     */
    default List<String> findTopHoldingSymbolsByValue(String portfolioId, int limit) {
        return findTopHoldingSymbolsByValue(portfolioId,
                org.springframework.data.domain.PageRequest.of(0, limit));
    }

    // Performance Queries

    /**
     * Find top performing portfolios for a user
     */
    @Query("SELECT p FROM Portfolio p WHERE p.user.id = :userId AND p.status = 'ACTIVE' " +
            "ORDER BY (" +
            "  (SELECT COALESCE(SUM(h.quantity * h.currentPrice), 0) FROM Holding h WHERE h.portfolio.id = p.id AND h.status = 'ACTIVE') - " +
            "  (SELECT COALESCE(SUM(h.quantity * h.averagePrice), 0) FROM Holding h WHERE h.portfolio.id = p.id AND h.status = 'ACTIVE')" +
            ") DESC")
    List<Portfolio> findTopPerformingPortfolios(@Param("userId") String userId, Pageable pageable);

    /**
     * Find top performing portfolios with limit
     */
    default List<Portfolio> findTopPerformingPortfolios(String userId, int limit) {
        return findTopPerformingPortfolios(userId,
                org.springframework.data.domain.PageRequest.of(0, limit));
    }

    /**
     * Find worst performing portfolios for a user
     */
    @Query("SELECT p FROM Portfolio p WHERE p.user.id = :userId AND p.status = 'ACTIVE' " +
            "ORDER BY (" +
            "  (SELECT COALESCE(SUM(h.quantity * h.currentPrice), 0) FROM Holding h WHERE h.portfolio.id = p.id AND h.status = 'ACTIVE') - " +
            "  (SELECT COALESCE(SUM(h.quantity * h.averagePrice), 0) FROM Holding h WHERE h.portfolio.id = p.id AND h.status = 'ACTIVE')" +
            ") ASC")
    List<Portfolio> findWorstPerformingPortfolios(@Param("userId") String userId, Pageable pageable);

    /**
     * Find worst performing portfolios with limit
     */
    default List<Portfolio> findWorstPerformingPortfolios(String userId, int limit) {
        return findWorstPerformingPortfolios(userId,
                org.springframework.data.domain.PageRequest.of(0, limit));
    }

    // Search Queries

    /**
     * Search user portfolios by name or description
     */
    @Query("SELECT p FROM Portfolio p WHERE p.user.id = :userId AND " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Portfolio> searchUserPortfolios(@Param("userId") String userId, @Param("searchTerm") String searchTerm);

    /**
     * Find portfolios by name containing (case-insensitive)
     */
    List<Portfolio> findByNameContainingIgnoreCase(String name);

    /**
     * Find portfolios by description containing (case-insensitive)
     */
    List<Portfolio> findByDescriptionContainingIgnoreCase(String description);

    // Date Range Queries

    /**
     * Find portfolios created between dates
     */
    List<Portfolio> findByCreatedAtBetween(Instant startDate, Instant endDate);

    /**
     * Find portfolios updated between dates
     */
    List<Portfolio> findByUpdatedAtBetween(Instant startDate, Instant endDate);

    // Public Portfolio Queries (if public portfolios are supported)

    /**
     * Find public portfolios
     */
    List<Portfolio> findByIsPublicTrueAndStatus(PortfolioStatus status);

    /**
     * Find public portfolios with pagination
     */
    Page<Portfolio> findByIsPublicTrueAndStatus(PortfolioStatus status, Pageable pageable);

    // Bulk Operations

    /**
     * Update portfolio status for multiple portfolios
     */
    @Query("UPDATE Portfolio p SET p.status = :status, p.updatedAt = :updatedAt WHERE p.id IN :portfolioIds")
    int updateStatusForPortfolios(@Param("portfolioIds") List<String> portfolioIds,
                                  @Param("status") PortfolioStatus status,
                                  @Param("updatedAt") Instant updatedAt);

    /**
     * Find portfolios with no active holdings
     */
    @Query("SELECT p FROM Portfolio p WHERE p.id NOT IN " +
            "(SELECT DISTINCT h.portfolio.id FROM Holding h WHERE h.status = 'ACTIVE')")
    List<Portfolio> findPortfoliosWithoutActiveHoldings();

    // Statistics

    /**
     * Get portfolio statistics for a user
     */
    @Query("SELECT " +
            "COUNT(p) as totalPortfolios, " +
            "COUNT(CASE WHEN p.status = 'ACTIVE' THEN 1 END) as activePortfolios, " +
            "COUNT(CASE WHEN p.status = 'ARCHIVED' THEN 1 END) as archivedPortfolios, " +
            "AVG(COALESCE((SELECT SUM(h.quantity * h.currentPrice) FROM Holding h WHERE h.portfolio.id = p.id AND h.status = 'ACTIVE'), 0)) as avgPortfolioValue " +
            "FROM Portfolio p WHERE p.user.id = :userId")
    Object[] getPortfolioStatistics(@Param("userId") String userId);

    /**
     * Get most traded symbols across all user portfolios
     */
    @Query("SELECT t.symbol, COUNT(t) as tradeCount FROM Transaction t " +
            "JOIN t.portfolio p WHERE p.user.id = :userId " +
            "GROUP BY t.symbol ORDER BY COUNT(t) DESC")
    List<Object[]> getMostTradedSymbols(@Param("userId") String userId, Pageable pageable);

    /**
     * Find portfolios that need attention (have holdings requiring attention)
     */
    @Query("SELECT DISTINCT p FROM Portfolio p JOIN p.holdings h " +
            "WHERE p.user.id = :userId AND h.status IN ('ERROR', 'UNDER_REVIEW', 'ON_HOLD', 'CORPORATE_ACTION', 'DELISTED')")
    List<Portfolio> findPortfoliosRequiringAttention(@Param("userId") String userId);
}