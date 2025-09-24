package com.portfolio.management.service;

public interface ValidationService {
    // User validation methods
    void validateUserRegistration(String username, String email, String password);

    void validatePasswordStrength(String password);

    void validateEmail(String email);

    void validateUsername(String username);

    void validateUserAccess(String requestedUserId, String currentUserId);

    // Business rule validation
    void validatePortfolioLimits(String userId, int currentPortfolioCount);

    void validateHoldingLimits(String portfolioId, int currentHoldingCount);

    void validateTransactionData(String symbol, java.math.BigDecimal quantity, java.math.BigDecimal price);

    void validateWatchlistLimits(String userId, int currentWatchlistCount);

    // Data validation
    boolean isValidSymbol(String symbol);

    boolean isValidCurrencyCode(String currency);

    boolean isValidCountryCode(String country);

    boolean isValidBrokerName(String brokerName);

    // Security validation
    void validateTokenFormat(String token);

    void validatePasswordChangeRequest(String oldPassword, String newPassword);

    // General validation utilities
    void validateNotNull(Object object, String fieldName);

    void validateNotEmpty(String value, String fieldName);

    void validatePositive(java.math.BigDecimal value, String fieldName);

    void validateDateRange(java.time.Instant startDate, java.time.Instant endDate);

    void validatePaginationParams(int page, int size);
}
