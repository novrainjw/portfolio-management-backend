package com.portfolio.management.service;

import com.portfolio.management.dto.request.PortfolioCreateRequest;
import com.portfolio.management.dto.request.PortfolioUpdateRequest;
import com.portfolio.management.dto.response.PortfolioResponse;
import com.portfolio.management.dto.response.PortfolioSummaryResponse;
import com.portfolio.management.entity.Portfolio;
import com.portfolio.management.enums.PortfolioStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Portfolio Service Interface
 * Handles all portfolio-related business operations
 */
public interface PortfolioService {

    // CRUD Operations
    PortfolioResponse createPortfolio(PortfolioCreateRequest request);

    PortfolioResponse updatePortfolio(String portfolioId, PortfolioUpdateRequest request);

    PortfolioResponse getPortfolioById(String portfolioId);

    Page<PortfolioResponse> getCurrentUserPortfolios(Pageable pageable);

    Page<PortfolioResponse> getUserPortfolios(String userId, Pageable pageable);

    void deletePortfolio(String portfolioId);

    void archivePortfolio(String portfolioId);

    void activatePortfolio(String portfolioId);

    // Portfolio Status Management
    void updatePortfolioStatus(String portfolioId, PortfolioStatus status);

    List<PortfolioResponse> getPortfoliosByStatus(PortfolioStatus status);

    List<PortfolioResponse> getActivePortfolios();

    // Portfolio Analytics & Performance
    PortfolioSummaryResponse getPortfolioSummary(String portfolioId);

    BigDecimal calculatePortfolioValue(String portfolioId);

    BigDecimal calculatePortfolioGainLoss(String portfolioId);

    BigDecimal calculatePortfolioGainLossPercentage(String portfolioId);

    BigDecimal calculateDayChange(String portfolioId);

    BigDecimal calculateDayChangePercentage(String portfolioId);

    // Portfolio Statistics
    int getHoldingsCount(String portfolioId);

    BigDecimal getTotalInvested(String portfolioId);

    BigDecimal getTotalDividends(String portfolioId);

    List<String> getTopHoldings(String portfolioId, int limit);

    // Portfolio Comparison & Analysis
    List<PortfolioSummaryResponse> comparePortfolios(List<String> portfolioIds);

    BigDecimal getPortfolioPerformance(String portfolioId, Instant startDate, Instant endDate);

    List<PortfolioResponse> getTopPerformingPortfolios(String userId, int limit);

    List<PortfolioResponse> getWorstPerformingPortfolios(String userId, int limit);

    // Portfolio Diversification
    Map<String, BigDecimal> getSectorAllocation(String portfolioId);

    Map<String, BigDecimal> getGeographicAllocation(String portfolioId);

    Map<String, BigDecimal> getAssetTypeAllocation(String portfolioId);

    boolean isPortfolioDiversified(String portfolioId);

    // Portfolio Validation & Limits
    void validatePortfolioAccess(String portfolioId);

    void validatePortfolioLimits(String userId);

    boolean canUserCreatePortfolio(String userId);

    boolean canUserModifyPortfolio(String portfolioId);

    // Search & Filter
    List<PortfolioResponse> searchPortfolios(String searchTerm);

    Page<PortfolioResponse> getPortfoliosWithFilters(String userId, PortfolioStatus status,
                                                     String currency, Instant createdAfter,
                                                     Instant updatedAfter, Pageable pageable);

    // Bulk Operations
    void updateMultiplePortfolioStatuses(List<String> portfolioIds, PortfolioStatus status);

    void deleteMultiplePortfolios(List<String> portfolioIds);

    List<PortfolioResponse> clonePortfolio(String portfolioId, String newName);

    // Portfolio Refresh & Sync
    void refreshPortfolioValues(String portfolioId);

    void refreshAllPortfolioValues(String userId);

    void syncPortfolioWithBroker(String portfolioId, String brokerId);

    // Utility Methods
    Portfolio getPortfolioEntityById(String portfolioId);

    boolean portfolioExists(String portfolioId);

    boolean isPortfolioOwnedByUser(String portfolioId, String userId);

    long getTotalPortfolioCount();

    long getActivePortfolioCount();

    long getUserPortfolioCount(String userId);
}