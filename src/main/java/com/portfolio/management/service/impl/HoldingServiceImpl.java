package com.portfolio.management.service.impl;

import com.portfolio.management.dto.request.HoldingCreateRequest;
import com.portfolio.management.dto.request.HoldingUpdateRequest;
import com.portfolio.management.dto.response.HoldingResponse;
import com.portfolio.management.dto.response.HoldingSummaryResponse;
import com.portfolio.management.entity.Holding;
import com.portfolio.management.entity.Portfolio;
import com.portfolio.management.enums.HoldingStatus;
import com.portfolio.management.exception.BadRequestException;
import com.portfolio.management.exception.InsufficientQuantityException;
import com.portfolio.management.exception.ResourceNotFoundException;
import com.portfolio.management.mapper.HoldingMapper;
import com.portfolio.management.repository.HoldingRepository;
import com.portfolio.management.security.UserPrincipal;
import com.portfolio.management.service.HoldingService;
import com.portfolio.management.service.MarketDataService;
import com.portfolio.management.service.PortfolioService;
import com.portfolio.management.service.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.portfolio.management.constants.AppConstants.CACHE_HOLDINGS;

/**
 * HoldingService Implementation
 * Handles all portfolio holding operations with comprehensive business logic
 */
@Service
@Transactional
public class HoldingServiceImpl implements HoldingService {

    private static final Logger logger = LoggerFactory.getLogger(HoldingServiceImpl.class);
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final HoldingRepository holdingRepository;
    private final HoldingMapper holdingMapper;
    private final PortfolioService portfolioService;
    private final MarketDataService marketDataService;
    private final ValidationService validationService;

    public HoldingServiceImpl(HoldingRepository holdingRepository,
                              HoldingMapper holdingMapper,
                              PortfolioService portfolioService,
                              MarketDataService marketDataService,
                              ValidationService validationService) {
        this.holdingRepository = holdingRepository;
        this.holdingMapper = holdingMapper;
        this.portfolioService = portfolioService;
        this.marketDataService = marketDataService;
        this.validationService = validationService;
    }

    // CRUD Operations

    @Override
    @Transactional
    public HoldingResponse addHolding(String portfolioId, HoldingCreateRequest request) {
        logger.info("Adding new holding {} to portfolio {}", request.getSymbol(), portfolioId);

        // Validate portfolio access
        portfolioService.validatePortfolioAccess(portfolioId);
        Portfolio portfolio = portfolioService.getPortfolioEntityById(portfolioId);

        // Validate request
        validateHoldingCreateRequest(request);

        // Check if holding already exists
        if (symbolExistsInPortfolio(portfolioId, request.getSymbol())) {
            throw new BadRequestException("Holding with symbol " + request.getSymbol() + " already exists in portfolio");
        }

        // Create holding entity
        Holding holding = holdingMapper.toEntity(request);
        holding.setPortfolio(portfolio);

        // Get current market price if not provided
        if (request.getCurrentPrice() == null) {
            BigDecimal marketPrice = marketDataService.getCurrentPrice(request.getSymbol());
            holding.setCurrentPrice(marketPrice);
        }

        // Set initial average price if not provided
        if (request.getAveragePrice() == null) {
            holding.setAveragePrice(holding.getCurrentPrice());
        }

        // Set sector and country from market data if not provided
        if (request.getSector() == null || request.getCountry() == null) {
            Map<String, String> companyInfo = marketDataService.getCompanyInfo(request.getSymbol());
            if (holding.getSector() == null) {
                holding.setSector(companyInfo.get("sector"));
            }
            if (holding.getCountry() == null) {
                holding.setCountry(companyInfo.get("country"));
            }
        }

        Holding savedHolding = holdingRepository.save(holding);
        logger.info("Holding {} created successfully with ID: {}", request.getSymbol(), savedHolding.getId());

        // Refresh portfolio totals
        portfolioService.refreshPortfolioValues(portfolioId);

        return holdingMapper.toResponse(savedHolding);
    }

    @Override
    @Transactional
    @CacheEvict(value = CACHE_HOLDINGS, key = "#holdingId")
    public HoldingResponse updateHolding(String holdingId, HoldingUpdateRequest request) {
        logger.info("Updating holding: {}", holdingId);

        Holding holding = getHoldingEntityById(holdingId);
        validateHoldingAccess(holdingId);

        // Update fields from request
        holdingMapper.updateEntityFromRequest(request, holding);

        Holding updatedHolding = holdingRepository.save(holding);
        logger.info("Holding {} updated successfully", holdingId);

        // Refresh portfolio totals
        portfolioService.refreshPortfolioValues(holding.getPortfolio().getId());

        return holdingMapper.toResponse(updatedHolding);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CACHE_HOLDINGS, key = "#holdingId")
    public HoldingResponse getHoldingById(String holdingId) {
        Holding holding = getHoldingEntityById(holdingId);
        validateHoldingAccess(holdingId);

        return createHoldingResponseWithCalculations(holding);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HoldingResponse> getPortfolioHoldings(String portfolioId) {
        portfolioService.validatePortfolioAccess(portfolioId);
        List<Holding> holdings = holdingRepository.findByPortfolioIdAndStatus(portfolioId, HoldingStatus.ACTIVE);

        return holdings.stream()
                .map(this::createHoldingResponseWithCalculations)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HoldingResponse> getPortfolioHoldings(String portfolioId, Pageable pageable) {
        portfolioService.validatePortfolioAccess(portfolioId);
        Page<Holding> holdings = holdingRepository.findByPortfolioIdAndStatus(portfolioId, HoldingStatus.ACTIVE, pageable);

        return holdings.map(this::createHoldingResponseWithCalculations);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HoldingResponse> getHoldingsBySymbol(String symbol) {
        UserPrincipal currentUser = getCurrentUserPrincipal();
        List<Holding> holdings = holdingRepository.findBySymbolAndPortfolioUserId(symbol, currentUser.getId());

        return holdings.stream()
                .map(this::createHoldingResponseWithCalculations)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HoldingResponse> getHoldingsByStatus(String portfolioId, HoldingStatus status) {
        portfolioService.validatePortfolioAccess(portfolioId);
        List<Holding> holdings = holdingRepository.findByPortfolioIdAndStatus(portfolioId, status);

        return holdings.stream()
                .map(holdingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = CACHE_HOLDINGS, key = "#holdingId")
    public void deleteHolding(String holdingId) {
        logger.info("Deleting holding: {}", holdingId);

        Holding holding = getHoldingEntityById(holdingId);
        validateHoldingAccess(holdingId);

        // Check if holding has non-zero quantity
        if (holding.getQuantity().compareTo(BigDecimal.ZERO) != 0) {
            throw new BadRequestException("Cannot delete holding with non-zero quantity. Current quantity: " + holding.getQuantity());
        }

        String portfolioId = holding.getPortfolio().getId();
        holdingRepository.delete(holding);

        logger.info("Holding {} deleted successfully", holdingId);

        // Refresh portfolio totals
        portfolioService.refreshPortfolioValues(portfolioId);
    }

    @Override
    @Transactional
    @CacheEvict(value = CACHE_HOLDINGS, key = "#holdingId")
    public void deactivateHolding(String holdingId) {
        logger.info("Deactivating holding: {}", holdingId);

        Holding holding = getHoldingEntityById(holdingId);
        validateHoldingAccess(holdingId);

        holding.setStatus(HoldingStatus.INACTIVE);
        holdingRepository.save(holding);

        // Refresh portfolio totals
        portfolioService.refreshPortfolioValues(holding.getPortfolio().getId());
    }

    @Override
    @Transactional
    @CacheEvict(value = CACHE_HOLDINGS, key = "#holdingId")
    public void reactivateHolding(String holdingId) {
        logger.info("Reactivating holding: {}", holdingId);

        Holding holding = getHoldingEntityById(holdingId);
        validateHoldingAccess(holdingId);

        holding.setStatus(HoldingStatus.ACTIVE);
        holdingRepository.save(holding);

        // Refresh portfolio totals
        portfolioService.refreshPortfolioValues(holding.getPortfolio().getId());
    }

    // Bulk Operations

    @Override
    @Transactional
    public void updateMultipleHoldingStatuses(List<String> holdingIds, HoldingStatus status) {
        holdingIds.forEach(id -> {
            if (status == HoldingStatus.ACTIVE) {
                reactivateHolding(id);
            } else {
                deactivateHolding(id);
            }
        });
    }

    @Override
    @Transactional
    public void deleteMultipleHoldings(List<String> holdingIds) {
        holdingIds.forEach(this::deleteHolding);
    }

    // Price and Value Operations

    @Override
    @Transactional
    @CacheEvict(value = CACHE_HOLDINGS, key = "#holdingId")
    public HoldingResponse updateHoldingPrice(String holdingId, BigDecimal newPrice) {
        logger.info("Updating price for holding {} to {}", holdingId, newPrice);

        Holding holding = getHoldingEntityById(holdingId);
        validateHoldingAccess(holdingId);

        if (newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Price must be greater than zero");
        }

        // Store previous price for day change calculation
        holding.setPreviousClosePrice(holding.getCurrentPrice());
        holding.setCurrentPrice(newPrice);
        holding.setUpdatedAt(Instant.now());

        Holding updatedHolding = holdingRepository.save(holding);

        // Refresh portfolio totals
        portfolioService.refreshPortfolioValues(holding.getPortfolio().getId());

        return createHoldingResponseWithCalculations(updatedHolding);
    }

    @Override
    @Transactional
    public void updatePortfolioHoldingPrices(String portfolioId) {
        logger.info("Updating all holding prices for portfolio: {}", portfolioId);

        portfolioService.validatePortfolioAccess(portfolioId);
        List<Holding> holdings = holdingRepository.findByPortfolioIdAndStatus(portfolioId, HoldingStatus.ACTIVE);

        for (Holding holding : holdings) {
            try {
                BigDecimal marketPrice = marketDataService.getCurrentPrice(holding.getSymbol());
                if (marketPrice != null) {
                    holding.setPreviousClosePrice(holding.getCurrentPrice());
                    holding.setCurrentPrice(marketPrice);
                    holding.setUpdatedAt(Instant.now());
                }
            } catch (Exception e) {
                logger.warn("Failed to update price for holding {}: {}", holding.getSymbol(), e.getMessage());
            }
        }

        holdingRepository.saveAll(holdings);
        portfolioService.refreshPortfolioValues(portfolioId);
    }

    @Override
    @Transactional
    public void updateHoldingPricesBySymbol(String symbol, BigDecimal newPrice) {
        logger.info("Updating price for all holdings of symbol {} to {}", symbol, newPrice);

        UserPrincipal currentUser = getCurrentUserPrincipal();
        List<Holding> holdings = holdingRepository.findBySymbolAndPortfolioUserId(symbol, currentUser.getId());

        for (Holding holding : holdings) {
            holding.setPreviousClosePrice(holding.getCurrentPrice());
            holding.setCurrentPrice(newPrice);
            holding.setUpdatedAt(Instant.now());
        }

        holdingRepository.saveAll(holdings);

        // Refresh portfolio totals for affected portfolios
        Set<String> portfolioIds = holdings.stream()
                .map(h -> h.getPortfolio().getId())
                .collect(Collectors.toSet());

        portfolioIds.forEach(portfolioService::refreshPortfolioValues);
    }

    @Override
    @Transactional
    public void refreshAllHoldingPrices() {
        logger.info("Refreshing all holding prices with market data");

        List<Holding> activeHoldings = holdingRepository.findByStatus(HoldingStatus.ACTIVE);
        Map<String, List<Holding>> holdingsBySymbol = activeHoldings.stream()
                .collect(Collectors.groupingBy(Holding::getSymbol));

        Set<String> affectedPortfolios = new HashSet<>();

        for (Map.Entry<String, List<Holding>> entry : holdingsBySymbol.entrySet()) {
            try {
                String symbol = entry.getKey();
                List<Holding> symbolHoldings = entry.getValue();

                BigDecimal marketPrice = marketDataService.getCurrentPrice(symbol);
                if (marketPrice != null) {
                    for (Holding holding : symbolHoldings) {
                        holding.setPreviousClosePrice(holding.getCurrentPrice());
                        holding.setCurrentPrice(marketPrice);
                        holding.setUpdatedAt(Instant.now());
                        affectedPortfolios.add(holding.getPortfolio().getId());
                    }
                }
            } catch (Exception e) {
                logger.warn("Failed to update prices for symbol {}: {}", entry.getKey(), e.getMessage());
            }
        }

        holdingRepository.saveAll(activeHoldings);

        // Refresh affected portfolios
        affectedPortfolios.forEach(portfolioService::refreshPortfolioValues);
    }

    // Financial Calculations

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateHoldingValue(String holdingId) {
        Holding holding = getHoldingEntityById(holdingId);
        validateHoldingAccess(holdingId);

        return holdingMapper.calculateTotalValue(holding.getQuantity(), holding.getCurrentPrice());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalInvested(String holdingId) {
        Holding holding = getHoldingEntityById(holdingId);
        validateHoldingAccess(holdingId);

        return holdingMapper.calculateTotalInvested(holding.getQuantity(), holding.getAveragePrice());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateUnrealizedGainLoss(String holdingId) {
        BigDecimal totalValue = calculateHoldingValue(holdingId);
        BigDecimal totalInvested = calculateTotalInvested(holdingId);

        return totalValue.subtract(totalInvested);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateUnrealizedGainLossPercentage(String holdingId) {
        BigDecimal totalInvested = calculateTotalInvested(holdingId);
        if (totalInvested.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal gainLoss = calculateUnrealizedGainLoss(holdingId);
        return gainLoss.divide(totalInvested, 4, RoundingMode.HALF_UP).multiply(HUNDRED);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateDayChange(String holdingId) {
        Holding holding = getHoldingEntityById(holdingId);
        validateHoldingAccess(holdingId);

        if (holding.getPreviousClosePrice() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal priceChange = holding.getCurrentPrice().subtract(holding.getPreviousClosePrice());
        return priceChange.multiply(holding.getQuantity());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateDayChangePercentage(String holdingId) {
        Holding holding = getHoldingEntityById(holdingId);
        validateHoldingAccess(holdingId);

        if (holding.getPreviousClosePrice() == null || holding.getPreviousClosePrice().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal priceChange = holding.getCurrentPrice().subtract(holding.getPreviousClosePrice());
        return priceChange.divide(holding.getPreviousClosePrice(), 4, RoundingMode.HALF_UP).multiply(HUNDRED);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculatePortfolioPercentage(String holdingId) {
        Holding holding = getHoldingEntityById(holdingId);
        validateHoldingAccess(holdingId);

        BigDecimal holdingValue = calculateHoldingValue(holdingId);
        BigDecimal portfolioValue = portfolioService.calculatePortfolioValue(holding.getPortfolio().getId());

        if (portfolioValue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return holdingValue.divide(portfolioValue, 4, RoundingMode.HALF_UP).multiply(HUNDRED);
    }

    @Override
    @Transactional
    @CacheEvict(value = CACHE_HOLDINGS, key = "#holdingId")
    public HoldingResponse recalculateHoldingMetrics(String holdingId) {
        Holding holding = getHoldingEntityById(holdingId);
        validateHoldingAccess(holdingId);

        // Update with latest market data if needed
        try {
            BigDecimal marketPrice = marketDataService.getCurrentPrice(holding.getSymbol());
            if (marketPrice != null && !marketPrice.equals(holding.getCurrentPrice())) {
                holding.setPreviousClosePrice(holding.getCurrentPrice());
                holding.setCurrentPrice(marketPrice);
                holding.setUpdatedAt(Instant.now());
            }
        } catch (Exception e) {
            logger.warn("Failed to update market price for holding {}: {}", holdingId, e.getMessage());
        }

        Holding updatedHolding = holdingRepository.save(holding);
        portfolioService.refreshPortfolioValues(holding.getPortfolio().getId());

        return createHoldingResponseWithCalculations(updatedHolding);
    }

    // Transaction Integration

    @Override
    @Transactional
    public HoldingResponse processBuyTransaction(String portfolioId, String symbol,
                                                 BigDecimal quantity, BigDecimal price,
                                                 BigDecimal fees, Instant transactionDate) {
        logger.info("Processing buy transaction: {} shares of {} at {}", quantity, symbol, price);

        portfolioService.validatePortfolioAccess(portfolioId);

        // Validate transaction data
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Price must be greater than zero");
        }

        // Check if holding already exists
        Optional<Holding> existingHolding = holdingRepository.findByPortfolioIdAndSymbol(portfolioId, symbol);

        if (existingHolding.isPresent()) {
            // Update existing holding
            Holding holding = existingHolding.get();
            updateAveragePrice(holding.getId(), holding.getQuantity().add(quantity), price, quantity);
            holding.setQuantity(holding.getQuantity().add(quantity));
            holding.setUpdatedAt(Instant.now());

            Holding updatedHolding = holdingRepository.save(holding);
            portfolioService.refreshPortfolioValues(portfolioId);

            return createHoldingResponseWithCalculations(updatedHolding);
        } else {
            // Create new holding
            HoldingCreateRequest createRequest = new HoldingCreateRequest();
            createRequest.setSymbol(symbol);
            createRequest.setQuantity(quantity);
            createRequest.setAveragePrice(price);
            createRequest.setCurrentPrice(price);

            return addHolding(portfolioId, createRequest);
        }
    }

    @Override
    @Transactional
    public HoldingResponse processSellTransaction(String holdingId, BigDecimal quantity,
                                                  BigDecimal price, BigDecimal fees,
                                                  Instant transactionDate) {
        logger.info("Processing sell transaction: {} shares from holding {} at {}", quantity, holdingId, price);

        Holding holding = getHoldingEntityById(holdingId);
        validateHoldingAccess(holdingId);

        // Validate sell quantity
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Sell quantity must be greater than zero");
        }
        if (quantity.compareTo(holding.getQuantity()) > 0) {
            throw new InsufficientQuantityException(
                    "Insufficient quantity. Available: " + holding.getQuantity() + ", Requested: " + quantity);
        }

        // Update holding quantity
        BigDecimal newQuantity = holding.getQuantity().subtract(quantity);
        holding.setQuantity(newQuantity);
        holding.setCurrentPrice(price);
        holding.setUpdatedAt(Instant.now());

        // If quantity becomes zero, deactivate holding
        if (newQuantity.compareTo(BigDecimal.ZERO) == 0) {
            holding.setStatus(HoldingStatus.SOLD_OUT);
        }

        Holding updatedHolding = holdingRepository.save(holding);
        portfolioService.refreshPortfolioValues(holding.getPortfolio().getId());

        return createHoldingResponseWithCalculations(updatedHolding);
    }

    @Override
    @Transactional
    public HoldingResponse processDividend(String holdingId, BigDecimal dividendPerShare,
                                           Instant exDividendDate, Instant paymentDate) {
        logger.info("Processing dividend for holding {}: {} per share", holdingId, dividendPerShare);

        Holding holding = getHoldingEntityById(holdingId);
        validateHoldingAccess(holdingId);

        // Calculate total dividend
        BigDecimal totalDividend = dividendPerShare.multiply(holding.getQuantity());

        // Update holding with dividend information
        holding.setLastDividendDate(paymentDate);
        holding.setUpdatedAt(Instant.now());

        Holding updatedHolding = holdingRepository.save(holding);
        portfolioService.refreshPortfolioValues(holding.getPortfolio().getId());

        logger.info("Dividend processed: {} total dividend for holding {}", totalDividend, holdingId);
        return createHoldingResponseWithCalculations(updatedHolding);
    }

    @Override
    @Transactional
    public HoldingResponse processStockSplit(String holdingId, BigDecimal splitRatio) {
        logger.info("Processing stock split for holding {}: ratio {}", holdingId, splitRatio);

        Holding holding = getHoldingEntityById(holdingId);
        validateHoldingAccess(holdingId);

        if (splitRatio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Split ratio must be greater than zero");
        }

        // Adjust quantity and prices for split
        holding.setQuantity(holding.getQuantity().multiply(splitRatio));
        holding.setAveragePrice(holding.getAveragePrice().divide(splitRatio, 4, RoundingMode.HALF_UP));
        holding.setCurrentPrice(holding.getCurrentPrice().divide(splitRatio, 4, RoundingMode.HALF_UP));

        if (holding.getPreviousClosePrice() != null) {
            holding.setPreviousClosePrice(holding.getPreviousClosePrice().divide(splitRatio, 4, RoundingMode.HALF_UP));
        }

        holding.setUpdatedAt(Instant.now());

        Holding updatedHolding = holdingRepository.save(holding);
        portfolioService.refreshPortfolioValues(holding.getPortfolio().getId());

        logger.info("Stock split processed for holding {}", holdingId);
        return createHoldingResponseWithCalculations(updatedHolding);
    }

    @Override
    @Transactional
    public void updateAveragePrice(String holdingId, BigDecimal newQuantity,
                                   BigDecimal transactionPrice, BigDecimal transactionQuantity) {
        Holding holding = getHoldingEntityById(holdingId);
        validateHoldingAccess(holdingId);

        if (newQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            return; // No update needed for zero quantity
        }

        // Calculate weighted average price
        BigDecimal currentValue = holding.getQuantity().multiply(holding.getAveragePrice());
        BigDecimal transactionValue = transactionQuantity.multiply(transactionPrice);
        BigDecimal totalValue = currentValue.add(transactionValue);

        BigDecimal newAveragePrice = totalValue.divide(newQuantity, 4, RoundingMode.HALF_UP);

        holding.setAveragePrice(newAveragePrice);
        holding.setUpdatedAt(Instant.now());

        holdingRepository.save(holding);
    }

    // Portfolio Analysis

    @Override
    @Transactional(readOnly = true)
    public List<HoldingResponse> getTopHoldingsByValue(String portfolioId, int limit) {
        portfolioService.validatePortfolioAccess(portfolioId);

        List<Holding> holdings = holdingRepository.findByPortfolioIdAndStatus(portfolioId, HoldingStatus.ACTIVE);

        return holdings.stream()
                .map(this::createHoldingResponseWithCalculations)
                .sorted((h1, h2) -> h2.getTotalValue().compareTo(h1.getTotalValue()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HoldingResponse> getTopHoldingsByGainLoss(String portfolioId, int limit) {
        portfolioService.validatePortfolioAccess(portfolioId);

        List<Holding> holdings = holdingRepository.findByPortfolioIdAndStatus(portfolioId, HoldingStatus.ACTIVE);

        return holdings.stream()
                .map(this::createHoldingResponseWithCalculations)
                .sorted((h1, h2) -> h2.getGainLoss().compareTo(h1.getGainLoss()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HoldingResponse> getWorstPerformingHoldings(String portfolioId, int limit) {
        portfolioService.validatePortfolioAccess(portfolioId);

        List<Holding> holdings = holdingRepository.findByPortfolioIdAndStatus(portfolioId, HoldingStatus.ACTIVE);

        return holdings.stream()
                .map(this::createHoldingResponseWithCalculations)
                .sorted(Comparator.comparing(HoldingResponse::getGainLoss))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HoldingSummaryResponse> getHoldingsSummary(String portfolioId) {
        portfolioService.validatePortfolioAccess(portfolioId);

        List<Holding> holdings = holdingRepository.findByPortfolioIdAndStatus(portfolioId, HoldingStatus.ACTIVE);

        return holdings.stream()
                .map(this::createHoldingSummaryResponse)
                .collect(Collectors.toList());
    }

    // Risk Management

    @Override
    @Transactional(readOnly = true)
    public List<HoldingResponse> getHoldingsAboveTargetPrice(String portfolioId) {
        portfolioService.validatePortfolioAccess(portfolioId);

        List<Holding> holdings = holdingRepository.findByPortfolioIdAndStatus(portfolioId, HoldingStatus.ACTIVE);

        return holdings.stream()
                .filter(h -> h.getTargetPrice() != null &&
                        h.getCurrentPrice().compareTo(h.getTargetPrice()) >= 0)
                .map(this::createHoldingResponseWithCalculations)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HoldingResponse> getHoldingsBelowStopLoss(String portfolioId) {
        portfolioService.validatePortfolioAccess(portfolioId);

        List<Holding> holdings = holdingRepository.findByPortfolioIdAndStatus(portfolioId, HoldingStatus.ACTIVE);

        return holdings.stream()
                .filter(h -> h.getStopLossPrice() != null &&
                        h.getCurrentPrice().compareTo(h.getStopLossPrice()) <= 0)
                .map(this::createHoldingResponseWithCalculations)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HoldingResponse> getHoldingsRequiringAttention(String portfolioId) {
        portfolioService.validatePortfolioAccess(portfolioId);

        List<HoldingResponse> attentionHoldings = new ArrayList<>();

        // Add holdings above target price
        attentionHoldings.addAll(getHoldingsAboveTargetPrice(portfolioId));

        // Add holdings below stop loss
        attentionHoldings.addAll(getHoldingsBelowStopLoss(portfolioId));

        // Add holdings with significant day losses (> 5%)
        List<Holding> holdings = holdingRepository.findByPortfolioIdAndStatus(portfolioId, HoldingStatus.ACTIVE);
        holdings.stream()
                .filter(h -> {
                    BigDecimal dayChangePercentage = calculateDayChangePercentage(h.getId());
                    return dayChangePercentage.compareTo(BigDecimal.valueOf(-5)) <= 0;
                })
                .map(this::createHoldingResponseWithCalculations)
                .forEach(attentionHoldings::add);

        // Remove duplicates and return
        return attentionHoldings.stream()
                .collect(Collectors.toMap(
                        HoldingResponse::getId,
                        h -> h,
                        (existing, replacement) -> existing))
                .values()
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isHoldingOverConcentrated(String holdingId, BigDecimal concentrationLimit) {
        BigDecimal portfolioPercentage = calculatePortfolioPercentage(holdingId);
        return portfolioPercentage.compareTo(concentrationLimit) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HoldingResponse> getOverconcentratedHoldings(String portfolioId, BigDecimal concentrationLimit) {
        portfolioService.validatePortfolioAccess(portfolioId);

        List<Holding> holdings = holdingRepository.findByPortfolioIdAndStatus(portfolioId, HoldingStatus.ACTIVE);

        return holdings.stream()
                .filter(h -> isHoldingOverConcentrated(h.getId(), concentrationLimit))
                .map(this::createHoldingResponseWithCalculations)
                .collect(Collectors.toList());
    }

    // Sector and Geographic Analysis

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<HoldingResponse>> getHoldingsBySector(String portfolioId) {
        portfolioService.validatePortfolioAccess(portfolioId);

        List<Holding> holdings = holdingRepository.findByPortfolioIdAndStatus(portfolioId, HoldingStatus.ACTIVE);

        return holdings.stream()
                .map(this::createHoldingResponseWithCalculations)
                .collect(Collectors.groupingBy(
                        h -> h.getSector() != null ? h.getSector() : "Unknown"
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<HoldingResponse>> getHoldingsByCountry(String portfolioId) {
        portfolioService.validatePortfolioAccess(portfolioId);

        List<Holding> holdings = holdingRepository.findByPortfolioIdAndStatus(portfolioId, HoldingStatus.ACTIVE);

        return holdings.stream()
                .map(this::createHoldingResponseWithCalculations)
                .collect(Collectors.groupingBy(
                        h -> h.getCountry() != null ? h.getCountry() : "Unknown"
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getSectorAllocation(String portfolioId) {
        portfolioService.validatePortfolioAccess(portfolioId);

        List<Holding> holdings = holdingRepository.findByPortfolioIdAndStatus(portfolioId, HoldingStatus.ACTIVE);
        BigDecimal totalValue = holdings.stream()
                .map(h -> calculateHoldingValue(h.getId()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalValue.compareTo(BigDecimal.ZERO) == 0) {
            return Collections.emptyMap();
        }

        return holdings.stream()
                .collect(Collectors.groupingBy(
                        h -> h.getSector() != null ? h.getSector() : "Unknown",
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                h -> calculateHoldingValue(h.getId())
                                        .divide(totalValue, 4, RoundingMode.HALF_UP)
                                        .multiply(HUNDRED),
                                BigDecimal::add
                        )
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getGeographicAllocation(String portfolioId) {
        portfolioService.validatePortfolioAccess(portfolioId);

        List<Holding> holdings = holdingRepository.findByPortfolioIdAndStatus(portfolioId, HoldingStatus.ACTIVE);
        BigDecimal totalValue = holdings.stream()
                .map(h -> calculateHoldingValue(h.getId()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalValue.compareTo(BigDecimal.ZERO) == 0) {
            return Collections.emptyMap();
        }

        return holdings.stream()
                .collect(Collectors.groupingBy(
                        h -> h.getCountry() != null ? h.getCountry() : "Unknown",
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                h -> calculateHoldingValue(h.getId())
                                        .divide(totalValue, 4, RoundingMode.HALF_UP)
                                        .multiply(HUNDRED),
                                BigDecimal::add
                        )
                ));
    }

    // Search and Filtering

    @Override
    @Transactional(readOnly = true)
    public List<HoldingResponse> searchHoldings(String portfolioId, String searchTerm) {
        portfolioService.validatePortfolioAccess(portfolioId);

        List<Holding> holdings = holdingRepository.searchHoldings(portfolioId, searchTerm);

        return holdings.stream()
                .map(this::createHoldingResponseWithCalculations)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HoldingResponse> getHoldingsWithFilters(String portfolioId, HoldingStatus status,
                                                        String sector, String country,
                                                        BigDecimal minValue, BigDecimal maxValue,
                                                        Instant createdAfter, Instant updatedAfter,
                                                        Pageable pageable) {
        portfolioService.validatePortfolioAccess(portfolioId);

        Specification<Holding> spec = Specification.where(null);

        spec = spec.and((root, query, cb) -> cb.equal(root.get("portfolio").get("id"), portfolioId));

        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        if (sector != null) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("sector")), "%" + sector.toLowerCase() + "%"));
        }

        if (country != null) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("country")), "%" + country.toLowerCase() + "%"));
        }

        if (createdAfter != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("createdAt"), createdAfter));
        }

        if (updatedAfter != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("updatedAt"), updatedAfter));
        }

        Page<Holding> holdings = holdingRepository.findAll(spec, pageable);

        return holdings.map(holding -> {
            HoldingResponse response = createHoldingResponseWithCalculations(holding);

            // Apply value filters after calculation
            if (minValue != null && response.getTotalValue().compareTo(minValue) < 0) {
                return null;
            }
            if (maxValue != null && response.getTotalValue().compareTo(maxValue) > 0) {
                return null;
            }

            return response;
        }).map(response -> response); // Filter out nulls would need custom implementation
    }

    // Performance Tracking

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getHoldingPerformance(String holdingId, Instant startDate, Instant endDate) {
        validateHoldingAccess(holdingId);
        validationService.validateDateRange(startDate, endDate);

        return holdingRepository.calculateHoldingPerformance(holdingId, startDate, endDate)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getHoldingPriceHistory(String holdingId,
                                                            Instant startDate, Instant endDate) {
        validateHoldingAccess(holdingId);
        validationService.validateDateRange(startDate, endDate);

        Holding holding = getHoldingEntityById(holdingId);

        // This would typically integrate with market data service to get historical prices
        // For now, return current price as single point
        Map<String, Object> currentPoint = new HashMap<>();
        currentPoint.put("date", Instant.now());
        currentPoint.put("price", holding.getCurrentPrice());
        currentPoint.put("volume", 0);

        return List.of(currentPoint);
    }

    // Validation and Utilities

    @Override
    public void validateHoldingAccess(String holdingId) {
        Holding holding = getHoldingEntityById(holdingId);
        portfolioService.validatePortfolioAccess(holding.getPortfolio().getId());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean holdingExists(String holdingId) {
        return holdingRepository.existsById(holdingId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isHoldingInPortfolio(String holdingId, String portfolioId) {
        return holdingRepository.existsByIdAndPortfolioId(holdingId, portfolioId);
    }

    @Override
    @Transactional(readOnly = true)
    public Holding getHoldingEntityById(String holdingId) {
        return holdingRepository.findById(holdingId)
                .orElseThrow(() -> new ResourceNotFoundException("Holding not found with ID: " + holdingId));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean symbolExistsInPortfolio(String portfolioId, String symbol) {
        return holdingRepository.existsByPortfolioIdAndSymbol(portfolioId, symbol);
    }

    @Override
    @Transactional(readOnly = true)
    public HoldingResponse getHoldingBySymbolInPortfolio(String portfolioId, String symbol) {
        portfolioService.validatePortfolioAccess(portfolioId);

        Optional<Holding> holding = holdingRepository.findByPortfolioIdAndSymbol(portfolioId, symbol);
        return holding.map(this::createHoldingResponseWithCalculations)
                .orElse(null);
    }

    // Synchronization

    @Override
    @Transactional
    public void syncHoldingWithBroker(String holdingId, String brokerId) {
        logger.info("Syncing holding {} with broker {}", holdingId, brokerId);

        validateHoldingAccess(holdingId);

        // Implementation would depend on broker integration
        // For now, just refresh with market data
        recalculateHoldingMetrics(holdingId);
    }

    @Override
    @Transactional
    public void syncPortfolioHoldingsWithBroker(String portfolioId, String brokerId) {
        logger.info("Syncing all holdings in portfolio {} with broker {}", portfolioId, brokerId);

        portfolioService.validatePortfolioAccess(portfolioId);
        updatePortfolioHoldingPrices(portfolioId);
    }

    // Market Data Integration

    @Override
    @Transactional
    public HoldingResponse updateHoldingWithMarketData(String holdingId) {
        logger.info("Updating holding {} with latest market data", holdingId);

        Holding holding = getHoldingEntityById(holdingId);
        validateHoldingAccess(holdingId);

        try {
            // Get latest market data
            BigDecimal marketPrice = marketDataService.getCurrentPrice(holding.getSymbol());
            Map<String, String> companyInfo = marketDataService.getCompanyInfo(holding.getSymbol());

            // Update price
            if (marketPrice != null) {
                holding.setPreviousClosePrice(holding.getCurrentPrice());
                holding.setCurrentPrice(marketPrice);
            }

            // Update company information if available
            if (companyInfo.get("sector") != null) {
                holding.setSector(companyInfo.get("sector"));
            }
            if (companyInfo.get("country") != null) {
                holding.setCountry(companyInfo.get("country"));
            }

            holding.setUpdatedAt(Instant.now());

            Holding updatedHolding = holdingRepository.save(holding);
            portfolioService.refreshPortfolioValues(holding.getPortfolio().getId());

            return createHoldingResponseWithCalculations(updatedHolding);

        } catch (Exception e) {
            logger.error("Failed to update holding {} with market data: {}", holdingId, e.getMessage());
            throw new BadRequestException("Failed to update holding with market data: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void scheduleHoldingPriceUpdates() {
        logger.info("Scheduling price updates for all active holdings");
        refreshAllHoldingPrices();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HoldingResponse> getStaleHoldings(Instant staleThreshold) {
        List<Holding> staleHoldings = holdingRepository.findByUpdatedAtBeforeAndStatus(staleThreshold, HoldingStatus.ACTIVE);

        return staleHoldings.stream()
                .map(holdingMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Statistics

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getHoldingStatistics(String portfolioId) {
        portfolioService.validatePortfolioAccess(portfolioId);

        List<Holding> holdings = holdingRepository.findByPortfolioIdAndStatus(portfolioId, HoldingStatus.ACTIVE);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalHoldings", holdings.size());
        stats.put("totalValue", holdings.stream()
                .map(h -> calculateHoldingValue(h.getId()))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        stats.put("totalInvested", holdings.stream()
                .map(h -> calculateTotalInvested(h.getId()))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        stats.put("uniqueSectors", holdings.stream()
                .map(Holding::getSector)
                .filter(Objects::nonNull)
                .distinct()
                .count());
        stats.put("uniqueCountries", holdings.stream()
                .map(Holding::getCountry)
                .filter(Objects::nonNull)
                .distinct()
                .count());

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public long getUserTotalHoldingsCount(String userId) {
        UserPrincipal currentUser = getCurrentUserPrincipal();
        if (!currentUser.getId().equals(userId)) {
            throw new BadRequestException("Access denied");
        }

        return holdingRepository.countByPortfolioUserIdAndStatus(userId, HoldingStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public int getActiveHoldingsCount(String portfolioId) {
        portfolioService.validatePortfolioAccess(portfolioId);
        return holdingRepository.countByPortfolioIdAndStatus(portfolioId, HoldingStatus.ACTIVE);
    }

    // Import/Export

    @Override
    @Transactional(readOnly = true)
    public byte[] exportHoldingsData(String portfolioId, String format) {
        portfolioService.validatePortfolioAccess(portfolioId);

        List<HoldingResponse> holdings = getPortfolioHoldings(portfolioId);

        // Implementation would depend on export format (CSV, Excel, JSON, etc.)
        // For now, return JSON as bytes
        try {
            // This would use a proper JSON library like Jackson
            String jsonData = holdings.toString(); // Simplified
            return jsonData.getBytes();
        } catch (Exception e) {
            throw new BadRequestException("Failed to export holdings data: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public List<HoldingResponse> importHoldingsData(String portfolioId, byte[] fileData, String format) {
        portfolioService.validatePortfolioAccess(portfolioId);

        // Implementation would depend on import format and parsing logic
        // This is a placeholder implementation
        throw new BadRequestException("Import functionality not yet implemented");
    }

    // Helper Methods

    private HoldingResponse createHoldingResponseWithCalculations(Holding holding) {
        BigDecimal totalValue = holdingMapper.calculateTotalValue(holding.getQuantity(), holding.getCurrentPrice());
        BigDecimal totalInvested = holdingMapper.calculateTotalInvested(holding.getQuantity(), holding.getAveragePrice());
        BigDecimal gainLoss = totalValue.subtract(totalInvested);
        BigDecimal gainLossPercentage = totalInvested.compareTo(BigDecimal.ZERO) == 0 ?
                BigDecimal.ZERO : gainLoss.divide(totalInvested, 4, RoundingMode.HALF_UP).multiply(HUNDRED);

        BigDecimal dayChange = BigDecimal.ZERO;
        BigDecimal dayChangePercentage = BigDecimal.ZERO;
        if (holding.getPreviousClosePrice() != null) {
            BigDecimal priceChange = holding.getCurrentPrice().subtract(holding.getPreviousClosePrice());
            dayChange = priceChange.multiply(holding.getQuantity());
            if (holding.getPreviousClosePrice().compareTo(BigDecimal.ZERO) > 0) {
                dayChangePercentage = priceChange.divide(holding.getPreviousClosePrice(), 4, RoundingMode.HALF_UP).multiply(HUNDRED);
            }
        }

        BigDecimal totalDividends = getTotalDividendsForHolding(holding.getId());
        Integer transactionCount = getTransactionCountForHolding(holding.getId());
        BigDecimal totalFees = getTotalFeesForHolding(holding.getId());
        BigDecimal portfolioPercentage = calculatePortfolioPercentage(holding.getId());

        return holdingMapper.toResponseWithFinancials(
                holding, totalValue, totalInvested, gainLoss, gainLossPercentage,
                dayChange, dayChangePercentage, totalDividends, transactionCount,
                totalFees, portfolioPercentage
        );
    }

    private HoldingSummaryResponse createHoldingSummaryResponse(Holding holding) {
        BigDecimal totalValue = holdingMapper.calculateTotalValue(holding.getQuantity(), holding.getCurrentPrice());
        BigDecimal totalInvested = holdingMapper.calculateTotalInvested(holding.getQuantity(), holding.getAveragePrice());
        BigDecimal gainLoss = totalValue.subtract(totalInvested);
        BigDecimal gainLossPercentage = totalInvested.compareTo(BigDecimal.ZERO) == 0 ?
                BigDecimal.ZERO : gainLoss.divide(totalInvested, 4, RoundingMode.HALF_UP).multiply(HUNDRED);

        BigDecimal dayChange = BigDecimal.ZERO;
        BigDecimal dayChangePercentage = BigDecimal.ZERO;
        if (holding.getPreviousClosePrice() != null) {
            BigDecimal priceChange = holding.getCurrentPrice().subtract(holding.getPreviousClosePrice());
            dayChange = priceChange.multiply(holding.getQuantity());
            if (holding.getPreviousClosePrice().compareTo(BigDecimal.ZERO) > 0) {
                dayChangePercentage = priceChange.divide(holding.getPreviousClosePrice(), 4, RoundingMode.HALF_UP).multiply(HUNDRED);
            }
        }

        BigDecimal portfolioPercentage = calculatePortfolioPercentage(holding.getId());
        Integer transactionCount = getTransactionCountForHolding(holding.getId());
        BigDecimal totalFees = getTotalFeesForHolding(holding.getId());
        BigDecimal totalDividends = getTotalDividendsForHolding(holding.getId());

        return holdingMapper.toSummaryResponse(
                holding, totalValue, totalInvested, gainLoss, gainLossPercentage,
                dayChange, dayChangePercentage, portfolioPercentage, transactionCount,
                totalFees, totalDividends
        );
    }

    private void validateHoldingCreateRequest(HoldingCreateRequest request) {
        validationService.validateNotEmpty(request.getSymbol(), "symbol");

        if (request.getQuantity() == null || request.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }

        if (request.getCurrentPrice() != null && request.getCurrentPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Current price must be greater than zero");
        }

        if (request.getAveragePrice() != null && request.getAveragePrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Average price must be greater than zero");
        }

        if (request.getTargetPrice() != null && request.getTargetPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Target price must be greater than zero");
        }

        if (request.getStopLossPrice() != null && request.getStopLossPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Stop loss price must be greater than zero");
        }
    }

    private BigDecimal getTotalDividendsForHolding(String holdingId) {
        // This would typically query the transaction repository for dividend transactions
        // For now, return zero as placeholder
        return BigDecimal.ZERO;
    }

    private Integer getTransactionCountForHolding(String holdingId) {
        // This would typically query the transaction repository for transaction count
        // For now, return zero as placeholder
        return 0;
    }

    private BigDecimal getTotalFeesForHolding(String holdingId) {
        // This would typically query the transaction repository for total fees
        // For now, return zero as placeholder
        return BigDecimal.ZERO;
    }

    private UserPrincipal getCurrentUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            throw new BadRequestException("No authenticated user found");
        }
        return (UserPrincipal) authentication.getPrincipal();
    }
}