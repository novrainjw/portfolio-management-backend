package com.portfolio.management.service.impl;

import com.portfolio.management.dto.request.PortfolioCreateRequest;
import com.portfolio.management.dto.request.PortfolioUpdateRequest;
import com.portfolio.management.dto.response.PortfolioResponse;
import com.portfolio.management.dto.response.PortfolioSummaryResponse;
import com.portfolio.management.entity.Portfolio;
import com.portfolio.management.entity.User;
import com.portfolio.management.enums.Currency;
import com.portfolio.management.enums.PortfolioStatus;
import com.portfolio.management.exception.BadRequestException;
import com.portfolio.management.exception.ResourceNotFoundException;
import com.portfolio.management.mapper.PortfolioMapper;
import com.portfolio.management.repository.PortfolioRepository;
import com.portfolio.management.security.UserPrincipal;
import com.portfolio.management.service.PortfolioService;
import com.portfolio.management.service.UserService;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.portfolio.management.constants.AppConstants.CACHE_PORTFOLIOS;

/**
 * Portfolio Service Implementation
 * Handles portfolio management operations using the actual Portfolio entity structure
 */
@Service
@Transactional
public class PortfolioServiceImpl implements PortfolioService {

    private static final Logger logger = LoggerFactory.getLogger(PortfolioServiceImpl.class);

    private final PortfolioRepository portfolioRepository;
    private final PortfolioMapper portfolioMapper;
    private final UserService userService;
    private final ValidationService validationService;

    public PortfolioServiceImpl(PortfolioRepository portfolioRepository,
                                PortfolioMapper portfolioMapper,
                                UserService userService,
                                ValidationService validationService) {
        this.portfolioRepository = portfolioRepository;
        this.portfolioMapper = portfolioMapper;
        this.userService = userService;
        this.validationService = validationService;
    }

    @Override
    @Transactional
    public PortfolioResponse createPortfolio(PortfolioCreateRequest request) {
        logger.info("Creating new portfolio with name: {}", request.getName());

        UserPrincipal currentUser = getCurrentUserPrincipal();

        // Validate portfolio limits
        validatePortfolioLimits(currentUser.getId());

        // Validate request data
        validationService.validateNotEmpty(request.getName(), "portfolio name");

        // Check if portfolio name already exists for this user
        if (portfolioRepository.existsByUserIdAndName(currentUser.getId(), request.getName())) {
            throw new BadRequestException("Portfolio with name '" + request.getName() + "' already exists");
        }

        // Create portfolio entity
        User user = userService.getUserEntityById(currentUser.getId());

        // Parse currency string to Currency enum
        Currency currency = Currency.USD; // Default
        if (request.getCurrency() != null) {
            try {
                currency = Currency.valueOf(request.getCurrency().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid currency: " + request.getCurrency());
            }
        }

        Portfolio portfolio = new Portfolio(
                request.getName(),
                request.getDescription(),
                user,
                "DEFAULT_BROKER", // You might want to get this from request or user settings
                currency
        );

        Portfolio savedPortfolio = portfolioRepository.save(portfolio);
        logger.info("Portfolio created successfully with ID: {}", savedPortfolio.getId());

        return portfolioMapper.toResponse(savedPortfolio);
    }

    @Override
    @Transactional
    @CacheEvict(value = CACHE_PORTFOLIOS, key = "#portfolioId")
    public PortfolioResponse updatePortfolio(String portfolioId, PortfolioUpdateRequest request) {
        logger.info("Updating portfolio: {}", portfolioId);

        Portfolio portfolio = getPortfolioEntityById(portfolioId);
        validatePortfolioAccess(portfolioId);

        // Validate request data
        if (request.getName() != null) {
            validationService.validateNotEmpty(request.getName(), "portfolio name");

            // Check if new name already exists for this user (excluding current portfolio)
            if (!request.getName().equals(portfolio.getName()) &&
                    portfolioRepository.existsByUserIdAndNameAndIdNot(portfolio.getUser().getId(), request.getName(), portfolioId)) {
                throw new BadRequestException("Portfolio with name '" + request.getName() + "' already exists");
            }
            portfolio.setName(request.getName());
        }

        if (request.getDescription() != null) {
            portfolio.setDescription(request.getDescription());
        }

        Portfolio updatedPortfolio = portfolioRepository.save(portfolio);
        logger.info("Portfolio updated successfully: {}", portfolioId);

        return portfolioMapper.toResponse(updatedPortfolio);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CACHE_PORTFOLIOS, key = "#portfolioId")
    public PortfolioResponse getPortfolioById(String portfolioId) {
        Portfolio portfolio = getPortfolioEntityById(portfolioId);
        validatePortfolioAccess(portfolioId);

        // Ensure portfolio totals are up to date
        portfolio.recalculateTotals();

        return portfolioMapper.toResponse(portfolio);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PortfolioResponse> getCurrentUserPortfolios(Pageable pageable) {
        UserPrincipal currentUser = getCurrentUserPrincipal();
        return getUserPortfolios(currentUser.getId(), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PortfolioResponse> getUserPortfolios(String userId, Pageable pageable) {
        // Only allow access to own portfolios unless admin
        if (!userService.isCurrentUser(userId)) {
            userService.validateUserAccess(userId);
        }

        Page<Portfolio> portfolios = portfolioRepository.findByUserId(userId, pageable);
        return portfolios.map(portfolioMapper::toResponse);
    }

    @Override
    @Transactional
    @CacheEvict(value = CACHE_PORTFOLIOS, key = "#portfolioId")
    public void deletePortfolio(String portfolioId) {
        logger.info("Deleting portfolio: {}", portfolioId);

        Portfolio portfolio = getPortfolioEntityById(portfolioId);
        validatePortfolioAccess(portfolioId);

        // Check if portfolio has active holdings
        if (portfolio.getHoldingsCount() > 0) {
            throw new BadRequestException("Cannot delete portfolio with active holdings. Please close all positions first.");
        }

        // Soft delete by deactivating
        portfolio.deactivate();
        portfolioRepository.save(portfolio);

        logger.info("Portfolio deleted (deactivated): {}", portfolioId);
    }

    @Override
    @Transactional
    @CacheEvict(value = CACHE_PORTFOLIOS, key = "#portfolioId")
    public void archivePortfolio(String portfolioId) {
        // Since Portfolio entity doesn't have archived status, we'll deactivate it
        deactivatePortfolio(portfolioId);
    }

    @Override
    @Transactional
    @CacheEvict(value = CACHE_PORTFOLIOS, key = "#portfolioId")
    public void activatePortfolio(String portfolioId) {
        logger.info("Activating portfolio: {}", portfolioId);

        Portfolio portfolio = getPortfolioEntityById(portfolioId);
        validatePortfolioAccess(portfolioId);

        portfolio.activate();
        portfolioRepository.save(portfolio);

        logger.info("Portfolio activated: {}", portfolioId);
    }

    // Helper method for deactivating portfolio
    @Transactional
    @CacheEvict(value = CACHE_PORTFOLIOS, key = "#portfolioId")
    public void deactivatePortfolio(String portfolioId) {
        logger.info("Deactivating portfolio: {}", portfolioId);

        Portfolio portfolio = getPortfolioEntityById(portfolioId);
        validatePortfolioAccess(portfolioId);

        portfolio.deactivate();
        portfolioRepository.save(portfolio);

        logger.info("Portfolio deactivated: {}", portfolioId);
    }

    @Override
    @Transactional
    @CacheEvict(value = CACHE_PORTFOLIOS, key = "#portfolioId")
    public void updatePortfolioStatus(String portfolioId, PortfolioStatus status) {
        // Convert PortfolioStatus enum to boolean operations
        switch (status) {
            case ACTIVE:
                activatePortfolio(portfolioId);
                break;
            case INACTIVE:
            case ARCHIVED:
            case CLOSED:
            case SUSPENDED:
            default:
                deactivatePortfolio(portfolioId);
                break;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PortfolioResponse> getPortfoliosByStatus(PortfolioStatus status) {
        UserPrincipal currentUser = getCurrentUserPrincipal();
        List<Portfolio> portfolios;

        if (status == PortfolioStatus.ACTIVE) {
//            portfolios = portfolioRepository.findByUserIdAndIsActiveTrue(currentUser.getId());
            portfolios = portfolioRepository.findByUserIdAndStatus(currentUser.getId(), PortfolioStatus.ACTIVE);
        } else {
//            portfolios = portfolioRepository.findByUserIdAndIsActiveFalse(currentUser.getId());
            portfolios = portfolioRepository.findByUserIdAndStatus(currentUser.getId(), PortfolioStatus.INACTIVE);
        }

        return portfolios.stream()
                .map(portfolioMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PortfolioResponse> getActivePortfolios() {
        return getPortfoliosByStatus(PortfolioStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public PortfolioSummaryResponse getPortfolioSummary(String portfolioId) {
        Portfolio portfolio = getPortfolioEntityById(portfolioId);
        validatePortfolioAccess(portfolioId);

        // Recalculate totals to ensure accuracy
        portfolio.recalculateTotals();

        // Get calculated values from portfolio entity
        BigDecimal totalValue = portfolio.getTotalValue();
        BigDecimal totalInvested = portfolio.getTotalCost();
        BigDecimal totalGainLoss = portfolio.getTotalGainLoss();
        BigDecimal gainLossPercentage = portfolio.getTotalGainLossPercent();
        BigDecimal dayChange = calculateDayChange(portfolioId);
        BigDecimal dayChangePercentage = calculateDayChangePercentage(portfolioId);
        BigDecimal totalDividends = getTotalDividends(portfolioId);
        Integer holdingsCount = portfolio.getHoldingsCount();
        Integer transactionsCount = portfolio.getTransactions().size();

        return portfolioMapper.toSummaryResponse(
                portfolio, totalValue, totalInvested, totalGainLoss,
                gainLossPercentage, dayChange, dayChangePercentage,
                totalDividends, holdingsCount, transactionsCount
        );
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculatePortfolioValue(String portfolioId) {
        Portfolio portfolio = getPortfolioEntityById(portfolioId);
        validatePortfolioAccess(portfolioId);

        portfolio.recalculateTotals();
        return portfolio.getTotalValue();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculatePortfolioGainLoss(String portfolioId) {
        Portfolio portfolio = getPortfolioEntityById(portfolioId);
        validatePortfolioAccess(portfolioId);

        portfolio.recalculateTotals();
        return portfolio.getTotalGainLoss();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculatePortfolioGainLossPercentage(String portfolioId) {
        Portfolio portfolio = getPortfolioEntityById(portfolioId);
        validatePortfolioAccess(portfolioId);

        portfolio.recalculateTotals();
        return portfolio.getTotalGainLossPercent();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateDayChange(String portfolioId) {
        validatePortfolioAccess(portfolioId);
        return portfolioRepository.calculateDayChange(portfolioId)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateDayChangePercentage(String portfolioId) {
        BigDecimal currentValue = calculatePortfolioValue(portfolioId);
        if (currentValue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal dayChange = calculateDayChange(portfolioId);
        BigDecimal previousValue = currentValue.subtract(dayChange);

        if (previousValue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return dayChange.divide(previousValue, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    @Override
    @Transactional(readOnly = true)
    public int getHoldingsCount(String portfolioId) {
        Portfolio portfolio = getPortfolioEntityById(portfolioId);
        validatePortfolioAccess(portfolioId);
        return portfolio.getHoldingsCount();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalInvested(String portfolioId) {
        Portfolio portfolio = getPortfolioEntityById(portfolioId);
        validatePortfolioAccess(portfolioId);
        portfolio.recalculateTotals();
        return portfolio.getTotalCost();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalDividends(String portfolioId) {
        validatePortfolioAccess(portfolioId);
        return portfolioRepository.calculateTotalDividends(portfolioId)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getTopHoldings(String portfolioId, int limit) {
        validatePortfolioAccess(portfolioId);
        return portfolioRepository.findTopHoldingSymbolsByValue(portfolioId, limit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PortfolioSummaryResponse> comparePortfolios(List<String> portfolioIds) {
        return portfolioIds.stream()
                .map(this::getPortfolioSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getPortfolioPerformance(String portfolioId, Instant startDate, Instant endDate) {
        validatePortfolioAccess(portfolioId);
        validationService.validateDateRange(startDate, endDate);

        return portfolioRepository.calculatePerformance(portfolioId, startDate, endDate)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PortfolioResponse> getTopPerformingPortfolios(String userId, int limit) {
        if (!userService.isCurrentUser(userId)) {
            userService.validateUserAccess(userId);
        }

        List<Portfolio> portfolios = portfolioRepository.findTopPerformingPortfolios(userId, limit);
        return portfolios.stream()
                .map(portfolioMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PortfolioResponse> getWorstPerformingPortfolios(String userId, int limit) {
        if (!userService.isCurrentUser(userId)) {
            userService.validateUserAccess(userId);
        }

        List<Portfolio> portfolios = portfolioRepository.findWorstPerformingPortfolios(userId, limit);
        return portfolios.stream()
                .map(portfolioMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getSectorAllocation(String portfolioId) {
        validatePortfolioAccess(portfolioId);
        return portfolioRepository.calculateSectorAllocation(portfolioId);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getGeographicAllocation(String portfolioId) {
        validatePortfolioAccess(portfolioId);
        return portfolioRepository.calculateGeographicAllocation(portfolioId);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getAssetTypeAllocation(String portfolioId) {
        validatePortfolioAccess(portfolioId);
        return portfolioRepository.calculateAssetTypeAllocation(portfolioId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPortfolioDiversified(String portfolioId) {
        Map<String, BigDecimal> sectorAllocation = getSectorAllocation(portfolioId);

        // Consider diversified if no single sector represents more than 40% of portfolio
        BigDecimal maxAllocation = sectorAllocation.values().stream()
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        return maxAllocation.compareTo(BigDecimal.valueOf(40)) <= 0;
    }

    @Override
    public void validatePortfolioAccess(String portfolioId) {
        Portfolio portfolio = getPortfolioEntityById(portfolioId);
        UserPrincipal currentUser = getCurrentUserPrincipal();

        if (!portfolio.getUser().getId().equals(currentUser.getId())) {
            // Check if user has admin role
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                throw new BadRequestException("Access denied: You can only access your own portfolios");
            }
        }
    }

    @Override
    public void validatePortfolioLimits(String userId) {
//        long userPortfolioCount = portfolioRepository.countByUserIdAndIsActiveTrue(userId);
        long userPortfolioCount = portfolioRepository.countByUserIdAndStatus(userId,PortfolioStatus.ACTIVE);
        validationService.validatePortfolioLimits(userId, (int) userPortfolioCount);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canUserCreatePortfolio(String userId) {
        try {
            validatePortfolioLimits(userId);
            return true;
        } catch (BadRequestException e) {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canUserModifyPortfolio(String portfolioId) {
        try {
            validatePortfolioAccess(portfolioId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PortfolioResponse> searchPortfolios(String searchTerm) {
        UserPrincipal currentUser = getCurrentUserPrincipal();
        List<Portfolio> portfolios = portfolioRepository.searchUserPortfolios(currentUser.getId(), searchTerm);
        return portfolios.stream()
                .map(portfolioMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PortfolioResponse> getPortfoliosWithFilters(String userId, PortfolioStatus status,
                                                            String currency, Instant createdAfter,
                                                            Instant updatedAfter, Pageable pageable) {
        if (!userService.isCurrentUser(userId)) {
            userService.validateUserAccess(userId);
        }

        Specification<Portfolio> spec = Specification.where(null);

        spec = spec.and((root, query, cb) -> cb.equal(root.get("user").get("id"), userId));

        // Convert PortfolioStatus to boolean for isActive field
        if (status != null) {
            boolean isActive = status == PortfolioStatus.ACTIVE;
            spec = spec.and((root, query, cb) -> cb.equal(root.get("isActive"), isActive));
        }

        if (currency != null) {
            try {
                Currency currencyEnum = Currency.valueOf(currency.toUpperCase());
                spec = spec.and((root, query, cb) -> cb.equal(root.get("currency"), currencyEnum));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid currency: " + currency);
            }
        }

        if (createdAfter != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), createdAfter));
        }

        if (updatedAfter != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("updatedAt"), updatedAfter));
        }

        Page<Portfolio> portfolios = portfolioRepository.findAll(spec, pageable);
        return portfolios.map(portfolioMapper::toResponse);
    }

    @Override
    @Transactional
    public void updateMultiplePortfolioStatuses(List<String> portfolioIds, PortfolioStatus status) {
        portfolioIds.forEach(id -> updatePortfolioStatus(id, status));
    }

    @Override
    @Transactional
    public void deleteMultiplePortfolios(List<String> portfolioIds) {
        portfolioIds.forEach(this::deletePortfolio);
    }

    @Override
    @Transactional
    public List<PortfolioResponse> clonePortfolio(String portfolioId, String newName) {
        Portfolio sourcePortfolio = getPortfolioEntityById(portfolioId);
        validatePortfolioAccess(portfolioId);

        // Create new portfolio with same settings
        PortfolioCreateRequest cloneRequest = new PortfolioCreateRequest();
        cloneRequest.setName(newName);
        cloneRequest.setDescription("Cloned from " + sourcePortfolio.getName());
        cloneRequest.setCurrency(sourcePortfolio.getCurrency().name());

        PortfolioResponse clonedPortfolio = createPortfolio(cloneRequest);

        return List.of(clonedPortfolio);
    }

    @Override
    @Transactional
    public void refreshPortfolioValues(String portfolioId) {
        logger.info("Refreshing portfolio values: {}", portfolioId);

        Portfolio portfolio = getPortfolioEntityById(portfolioId);
        validatePortfolioAccess(portfolioId);

        portfolio.recalculateTotals();
        portfolioRepository.save(portfolio);
    }

    @Override
    @Transactional
    public void refreshAllPortfolioValues(String userId) {
//        List<Portfolio> userPortfolios = portfolioRepository.findByUserIdAndIsActiveTrue(userId);
        List<Portfolio> userPortfolios = portfolioRepository.findByUserIdAndStatus(userId,PortfolioStatus.ACTIVE);
        userPortfolios.forEach(portfolio -> refreshPortfolioValues(portfolio.getId()));
    }

    @Override
    @Transactional
    public void syncPortfolioWithBroker(String portfolioId, String brokerId) {
        logger.info("Syncing portfolio {} with broker {}", portfolioId, brokerId);
        validatePortfolioAccess(portfolioId);

        // Implementation would depend on broker integration
        refreshPortfolioValues(portfolioId);
    }

    // Utility Methods

    @Override
    @Transactional(readOnly = true)
    public Portfolio getPortfolioEntityById(String portfolioId) {
        return portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with ID: " + portfolioId));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean portfolioExists(String portfolioId) {
        return portfolioRepository.existsById(portfolioId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPortfolioOwnedByUser(String portfolioId, String userId) {
        return portfolioRepository.existsByIdAndUserId(portfolioId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalPortfolioCount() {
        return portfolioRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getActivePortfolioCount() {
//        return portfolioRepository.countByIsActiveTrue();
        return portfolioRepository.countByStatus(PortfolioStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUserPortfolioCount(String userId) {
        return portfolioRepository.countByUserId(userId);
    }

    // Helper Methods

    private UserPrincipal getCurrentUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            throw new BadRequestException("No authenticated user found");
        }
        return (UserPrincipal) authentication.getPrincipal();
    }
}