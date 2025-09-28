package com.portfolio.management.constants;

/**
 * Application-wide constants
 */
public final class AppConstants {

    private AppConstants() {
        // Utility class - prevent instantiation
    }

    // Cache Names
    public static final String CACHE_PORTFOLIOS = "portfolios";
    public static final String CACHE_HOLDINGS = "holdings";
    public static final String CACHE_TRANSACTIONS = "transactions";
    public static final String CACHE_USERS = "users";
    public static final String CACHE_MARKET_DATA = "market_data";

    // Market Data Cache Names
    public static final String CACHE_MARKET_PRICES = "market_prices";
    public static final String CACHE_MARKET_PRICES_BATCH = "market_prices_batch";
    public static final String CACHE_COMPANY_INFO = "company_info";
    public static final String CACHE_MARKET_STATUS = "market_status";
    public static final String CACHE_MARKET_HOURS = "market_hours";
    public static final String CACHE_SYMBOL_SEARCH = "symbol_search";
    public static final String CACHE_SYMBOL_VALIDATION = "symbol_validation";

    // Portfolio Limits
    public static final int DEFAULT_MAX_PORTFOLIOS_PER_USER = 10;
    public static final int PREMIUM_MAX_PORTFOLIOS_PER_USER = 50;
    public static final int DEFAULT_MAX_HOLDINGS_PER_PORTFOLIO = 100;
    public static final int PREMIUM_MAX_HOLDINGS_PER_PORTFOLIO = 500;

    // Pagination Defaults
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    public static final String DEFAULT_SORT_DIRECTION = "DESC";
    public static final String DEFAULT_PORTFOLIO_SORT = "updatedAt";
    public static final String DEFAULT_HOLDING_SORT = "totalValue";
    public static final String DEFAULT_TRANSACTION_SORT = "transactionDate";

    // Financial Calculation Precision
    public static final int PRICE_SCALE = 2;
    public static final int PERCENTAGE_SCALE = 4;
    public static final int QUANTITY_SCALE = 6;
    public static final int CALCULATION_SCALE = 8;

    // Risk Management
    public static final double DEFAULT_CONCENTRATION_LIMIT = 20.0; // 20% of portfolio
    public static final double HIGH_CONCENTRATION_LIMIT = 30.0; // 30% of portfolio
    public static final double DAY_CHANGE_ALERT_THRESHOLD = 5.0; // 5% day change
    public static final double GAIN_LOSS_ALERT_THRESHOLD = 10.0; // 10% gain/loss

    // Market Hours (EST/EDT)
    public static final String MARKET_TIMEZONE = "America/New_York";
    public static final String MARKET_OPEN_TIME = "09:30";
    public static final String MARKET_CLOSE_TIME = "16:00";
    public static final String EXTENDED_OPEN_TIME = "04:00";
    public static final String EXTENDED_CLOSE_TIME = "20:00";

    // Data Validation
    public static final int MIN_SYMBOL_LENGTH = 1;
    public static final int MAX_SYMBOL_LENGTH = 10;
    public static final int MIN_COMPANY_NAME_LENGTH = 1;
    public static final int MAX_COMPANY_NAME_LENGTH = 255;
    public static final int MAX_PORTFOLIO_NAME_LENGTH = 100;
    public static final int MAX_PORTFOLIO_DESCRIPTION_LENGTH = 500;
    public static final int MAX_NOTES_LENGTH = 1000;

    // Security
    public static final String JWT_SECRET_KEY = "portfolio_management_jwt_secret_key_2024";
    public static final long JWT_EXPIRATION_TIME = 86400000; // 24 hours in milliseconds
    public static final String JWT_TOKEN_PREFIX = "Bearer ";
    public static final String JWT_HEADER_STRING = "Authorization";

    // API Versioning
    public static final String API_VERSION_V1 = "/api/v1";
    public static final String API_VERSION_V2 = "/api/v2";

    // Error Codes
    public static final String ERROR_INVALID_REQUEST = "INVALID_REQUEST";
    public static final String ERROR_RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND";
    public static final String ERROR_UNAUTHORIZED = "UNAUTHORIZED";
    public static final String ERROR_FORBIDDEN = "FORBIDDEN";
    public static final String ERROR_DUPLICATE_RESOURCE = "DUPLICATE_RESOURCE";
    public static final String ERROR_INSUFFICIENT_QUANTITY = "INSUFFICIENT_QUANTITY";
    public static final String ERROR_MARKET_DATA_UNAVAILABLE = "MARKET_DATA_UNAVAILABLE";
    public static final String ERROR_PORTFOLIO_LIMIT_EXCEEDED = "PORTFOLIO_LIMIT_EXCEEDED";
    public static final String ERROR_HOLDING_LIMIT_EXCEEDED = "HOLDING_LIMIT_EXCEEDED";

    // File Upload
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    public static final String[] ALLOWED_FILE_TYPES = {"text/csv", "application/vnd.ms-excel", "application/json"};

    // Email Templates
    public static final String EMAIL_TEMPLATE_WELCOME = "welcome";
    public static final String EMAIL_TEMPLATE_PASSWORD_RESET = "password_reset";
    public static final String EMAIL_TEMPLATE_PORTFOLIO_ALERT = "portfolio_alert";
    public static final String EMAIL_TEMPLATE_DAILY_SUMMARY = "daily_summary";

    // Notification Types
    public static final String NOTIFICATION_TYPE_PRICE_ALERT = "PRICE_ALERT";
    public static final String NOTIFICATION_TYPE_TARGET_REACHED = "TARGET_REACHED";
    public static final String NOTIFICATION_TYPE_STOP_LOSS_HIT = "STOP_LOSS_HIT";
    public static final String NOTIFICATION_TYPE_PORTFOLIO_SUMMARY = "PORTFOLIO_SUMMARY";
    public static final String NOTIFICATION_TYPE_DIVIDEND_RECEIVED = "DIVIDEND_RECEIVED";
    public static final String NOTIFICATION_TYPE_STOCK_SPLIT = "STOCK_SPLIT";

    // Schedule Intervals
    public static final String PRICE_UPDATE_SCHEDULE = "0 */5 * * * *"; // Every 5 minutes
    public static final String DAILY_SUMMARY_SCHEDULE = "0 0 18 * * *"; // 6 PM daily
    public static final String PORTFOLIO_CLEANUP_SCHEDULE = "0 0 2 * * *"; // 2 AM daily
    public static final String CACHE_CLEANUP_SCHEDULE = "0 0 1 * * *"; // 1 AM daily

    // Default Values
    public static final String DEFAULT_CURRENCY = "USD";
    public static final String DEFAULT_COUNTRY = "US";
    public static final String DEFAULT_SECTOR = "Unknown";
    public static final String DEFAULT_EXCHANGE = "NASDAQ";

    // Business Rules
    public static final double MIN_TRADE_AMOUNT = 1.00;
    public static final double MIN_STOCK_PRICE = 0.01;
    public static final double MAX_STOCK_PRICE = 999999.99;
    public static final double MIN_QUANTITY = 0.000001;
    public static final double MAX_QUANTITY = 999999999.999999;

    // Export/Import
    public static final String EXPORT_FORMAT_CSV = "CSV";
    public static final String EXPORT_FORMAT_EXCEL = "EXCEL";
    public static final String EXPORT_FORMAT_JSON = "JSON";
    public static final String EXPORT_FORMAT_PDF = "PDF";

    // Logging
    public static final String LOG_TRANSACTION_CREATE = "Transaction created: {}";
    public static final String LOG_TRANSACTION_UPDATE = "Transaction updated: {}";
    public static final String LOG_TRANSACTION_DELETE = "Transaction deleted: {}";
    public static final String LOG_PORTFOLIO_CREATE = "Portfolio created: {}";
    public static final String LOG_PORTFOLIO_UPDATE = "Portfolio updated: {}";
    public static final String LOG_PORTFOLIO_DELETE = "Portfolio deleted: {}";
    public static final String LOG_HOLDING_CREATE = "Holding created: {}";
    public static final String LOG_HOLDING_UPDATE = "Holding updated: {}";
    public static final String LOG_HOLDING_DELETE = "Holding deleted: {}";
    public static final String LOG_PRICE_UPDATE = "Price updated for symbol: {}";
    public static final String LOG_USER_LOGIN = "User logged in: {}";
    public static final String LOG_USER_LOGOUT = "User logged out: {}";
    public static final String LOG_MARKET_DATA_FETCH = "Market data fetched for: {}";
}