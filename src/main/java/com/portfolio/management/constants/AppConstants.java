package com.portfolio.management.constants;

public final class AppConstants {
    // API
    public static final String API_V1_PREFIX = "/api/v1";
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "20";
    public static final String DEFAULT_SORT_BY = "createdAt";
    public static final String DEFAULT_SORT_DIRECTION = "desc";

    // Cache Names
    public static final String CACHE_PORTFOLIOS = "portfolios";
    public static final String CACHE_HOLDINGS = "holdings";
    public static final String CACHE_USERS = "users";
    public static final String CACHE_TRANSACTIONS = "transactions";
    public static final String CACHE_WATCHLIST = "watchlist";

    // Security
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    // Date/Time
    public static final String DEFAULT_TIME_ZONE = "UTC";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    // Business Rules
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PORTFOLIOS_PER_USER = 10;
    public static final int MAX_HOLDINGS_PER_PORTFOLIO = 100;
    public static final int MAX_WATCHLIST_ITEMS = 50;

    private AppConstants() {
        // Private constructor to prevent instantiation
    }
}
