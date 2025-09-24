package com.portfolio.management.service.impl;

import com.portfolio.management.constants.AppConstants;
import com.portfolio.management.exception.BadRequestException;
import com.portfolio.management.service.UserService;
import com.portfolio.management.service.ValidationService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

@Service
public class ValidationServiceImpl implements ValidationService {

    private final UserService userService;

    // Validation patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    private static final Pattern USERNAME_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._-]{3,50}$"
    );

    private static final Pattern SYMBOL_PATTERN = Pattern.compile(
            "^[A-Z]{1,10}$"
    );

    private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );

    private static final Pattern CURRENCY_CODE_PATTERN = Pattern.compile(
            "^[A-Z]{3}$"
    );

    private static final Pattern COUNTRY_CODE_PATTERN = Pattern.compile(
            "^[A-Z]{2}$"
    );

    // Constants
    private static final int MAX_PAGE_SIZE = 100;
    private static final int MIN_PAGE_SIZE = 1;
    private static final BigDecimal MAX_TRANSACTION_AMOUNT = new BigDecimal("1000000000"); // 1 billion
    private static final BigDecimal MIN_TRANSACTION_AMOUNT = new BigDecimal("0.01");

    public ValidationServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void validateUserRegistration(String username, String email, String password) {
        validateUsername(username);
        validateEmail(email);
        validatePasswordStrength(password);

        // Check if username or email already exists
        if (userService.existsByUsername(username)) {
            throw new BadRequestException("Username '" + username + "' is already taken");
        }

        if (userService.existsByEmail(email)) {
            throw new BadRequestException("Email '" + email + "' is already registered");
        }
    }

    @Override
    public void validatePasswordStrength(String password) {
        validateNotEmpty(password, "password");

        if (password.length() < AppConstants.MIN_PASSWORD_LENGTH) {
            throw new BadRequestException("Password must be at least " + AppConstants.MIN_PASSWORD_LENGTH + " characters long");
        }

        if (password.length() > 128) {
            throw new BadRequestException("Password cannot exceed 128 characters");
        }

        // Check for strong password requirements
        if (!STRONG_PASSWORD_PATTERN.matcher(password).matches()) {
            throw new BadRequestException(
                    "Password must contain at least one lowercase letter, one uppercase letter, " +
                            "one digit, and one special character (@$!%*?&)"
            );
        }

        // Check for common weak passwords
        if (isCommonPassword(password)) {
            throw new BadRequestException("Password is too common. Please choose a stronger password");
        }
    }

    @Override
    public void validateEmail(String email) {
        validateNotEmpty(email, "email");

        if (email.length() > 254) {
            throw new BadRequestException("Email address cannot exceed 254 characters");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BadRequestException("Invalid email format");
        }

        // Additional email validation
        if (email.contains("..")) {
            throw new BadRequestException("Email cannot contain consecutive dots");
        }

        if (email.startsWith(".") || email.endsWith(".")) {
            throw new BadRequestException("Email cannot start or end with a dot");
        }
    }

    @Override
    public void validateUsername(String username) {
        validateNotEmpty(username, "username");

        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new BadRequestException(
                    "Username must be 3-50 characters long and can only contain letters, " +
                            "numbers, dots, underscores, and hyphens"
            );
        }

        // Check for reserved usernames
        if (isReservedUsername(username)) {
            throw new BadRequestException("Username '" + username + "' is reserved");
        }
    }

    @Override
    public void validateUserAccess(String requestedUserId, String currentUserId) {
        if (!requestedUserId.equals(currentUserId)) {
            throw new BadRequestException("Access denied: You can only access your own data");
        }
    }

    @Override
    public void validatePortfolioLimits(String userId, int currentPortfolioCount) {
        if (currentPortfolioCount >= AppConstants.MAX_PORTFOLIOS_PER_USER) {
            throw new BadRequestException(
                    "Maximum portfolio limit reached. You can have up to " +
                            AppConstants.MAX_PORTFOLIOS_PER_USER + " portfolios"
            );
        }
    }

    @Override
    public void validateHoldingLimits(String portfolioId, int currentHoldingCount) {
        if (currentHoldingCount >= AppConstants.MAX_HOLDINGS_PER_PORTFOLIO) {
            throw new BadRequestException(
                    "Maximum holding limit reached for this portfolio. You can have up to " +
                            AppConstants.MAX_HOLDINGS_PER_PORTFOLIO + " holdings per portfolio"
            );
        }
    }

    @Override
    public void validateTransactionData(String symbol, BigDecimal quantity, BigDecimal price) {
        validateNotEmpty(symbol, "symbol");
        validateNotNull(quantity, "quantity");
        validateNotNull(price, "price");

        if (!isValidSymbol(symbol)) {
            throw new BadRequestException("Invalid stock symbol: " + symbol);
        }

        validatePositive(quantity, "quantity");
        validatePositive(price, "price");

        // Calculate total amount
        BigDecimal totalAmount = quantity.multiply(price);

        if (totalAmount.compareTo(MIN_TRANSACTION_AMOUNT) < 0) {
            throw new BadRequestException("Transaction amount is too small. Minimum: $" + MIN_TRANSACTION_AMOUNT);
        }

        if (totalAmount.compareTo(MAX_TRANSACTION_AMOUNT) > 0) {
            throw new BadRequestException("Transaction amount is too large. Maximum: $" + MAX_TRANSACTION_AMOUNT);
        }
    }

    @Override
    public void validateWatchlistLimits(String userId, int currentWatchlistCount) {
        if (currentWatchlistCount >= AppConstants.MAX_WATCHLIST_ITEMS) {
            throw new BadRequestException(
                    "Maximum watchlist limit reached. You can have up to " +
                            AppConstants.MAX_WATCHLIST_ITEMS + " items in your watchlist"
            );
        }
    }

    @Override
    public boolean isValidSymbol(String symbol) {
        return StringUtils.hasText(symbol) && SYMBOL_PATTERN.matcher(symbol.toUpperCase()).matches();
    }

    @Override
    public boolean isValidCurrencyCode(String currency) {
        return StringUtils.hasText(currency) && CURRENCY_CODE_PATTERN.matcher(currency.toUpperCase()).matches();
    }

    @Override
    public boolean isValidCountryCode(String country) {
        return StringUtils.hasText(country) && COUNTRY_CODE_PATTERN.matcher(country.toUpperCase()).matches();
    }

    @Override
    public boolean isValidBrokerName(String brokerName) {
        return StringUtils.hasText(brokerName) &&
                brokerName.trim().length() >= 2 &&
                brokerName.trim().length() <= 100 &&
                brokerName.matches("^[a-zA-Z0-9\\s\\-&.]+$");
    }

    @Override
    public void validateTokenFormat(String token) {
        if (!StringUtils.hasText(token)) {
            throw new BadRequestException("Token cannot be empty");
        }

        if (token.length() < 10) {
            throw new BadRequestException("Invalid token format");
        }

        // Basic JWT format check (header.payload.signature)
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new BadRequestException("Invalid JWT token format");
        }
    }

    @Override
    public void validatePasswordChangeRequest(String oldPassword, String newPassword) {
        validateNotEmpty(oldPassword, "current password");
        validatePasswordStrength(newPassword);

        if (oldPassword.equals(newPassword)) {
            throw new BadRequestException("New password must be different from the current password");
        }
    }

    @Override
    public void validateNotNull(Object object, String fieldName) {
        if (object == null) {
            throw new BadRequestException(StringUtils.capitalize(fieldName) + " cannot be null");
        }
    }

    @Override
    public void validateNotEmpty(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            throw new BadRequestException(StringUtils.capitalize(fieldName) + " cannot be empty");
        }
    }

    @Override
    public void validatePositive(BigDecimal value, String fieldName) {
        validateNotNull(value, fieldName);
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException(StringUtils.capitalize(fieldName) + " must be positive");
        }
    }

    @Override
    public void validateDateRange(Instant startDate, Instant endDate) {
        if (startDate != null && endDate != null) {
            if (startDate.isAfter(endDate)) {
                throw new BadRequestException("Start date cannot be after end date");
            }

            // Check if date range is reasonable (not more than 10 years)
            if (ChronoUnit.DAYS.between(startDate, endDate) > 3650) {
                throw new BadRequestException("Date range cannot exceed 10 years");
            }
        }

        // Validate that dates are not too far in the future
        Instant now = Instant.now();
        if (startDate != null && startDate.isAfter(now.plus(1, ChronoUnit.DAYS))) {
            throw new BadRequestException("Start date cannot be more than 1 day in the future");
        }

        if (endDate != null && endDate.isAfter(now.plus(1, ChronoUnit.DAYS))) {
            throw new BadRequestException("End date cannot be more than 1 day in the future");
        }
    }

    @Override
    public void validatePaginationParams(int page, int size) {
        if (page < 0) {
            throw new BadRequestException("Page number cannot be negative");
        }

        if (size < MIN_PAGE_SIZE) {
            throw new BadRequestException("Page size must be at least " + MIN_PAGE_SIZE);
        }

        if (size > MAX_PAGE_SIZE) {
            throw new BadRequestException("Page size cannot exceed " + MAX_PAGE_SIZE);
        }
    }

    // Helper methods

    private boolean isCommonPassword(String password) {
        // List of common weak passwords
        String[] commonPasswords = {
                "password", "123456", "password123", "admin", "qwerty",
                "letmein", "welcome", "monkey", "dragon", "master"
        };

        String lowerPassword = password.toLowerCase();
        for (String common : commonPasswords) {
            if (lowerPassword.contains(common)) {
                return true;
            }
        }

        return false;
    }

    private boolean isReservedUsername(String username) {
        // List of reserved usernames
        String[] reservedUsernames = {
                "admin", "administrator", "root", "system", "api", "support",
                "help", "info", "contact", "sales", "marketing", "service",
                "user", "guest", "anonymous", "null", "undefined", "test"
        };

        String lowerUsername = username.toLowerCase();
        for (String reserved : reservedUsernames) {
            if (lowerUsername.equals(reserved)) {
                return true;
            }
        }

        return false;
    }
}
