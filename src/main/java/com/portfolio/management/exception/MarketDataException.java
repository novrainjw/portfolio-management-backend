package com.portfolio.management.exception;

/**
 * Exception thrown when market data operations fail
 */
public class MarketDataException extends RuntimeException {

    private final String symbol;
    private final String operation;
    private final String provider;

    public MarketDataException(String message) {
        super(message);
        this.symbol = null;
        this.operation = null;
        this.provider = null;
    }

    public MarketDataException(String message, Throwable cause) {
        super(message, cause);
        this.symbol = null;
        this.operation = null;
        this.provider = null;
    }

    public MarketDataException(String message, String symbol) {
        super(message);
        this.symbol = symbol;
        this.operation = null;
        this.provider = null;
    }

    public MarketDataException(String message, String symbol, Throwable cause) {
        super(message, cause);
        this.symbol = symbol;
        this.operation = null;
        this.provider = null;
    }

    public MarketDataException(String message, String symbol, String operation, String provider) {
        super(message);
        this.symbol = symbol;
        this.operation = operation;
        this.provider = provider;
    }

    public MarketDataException(String message, String symbol, String operation, String provider, Throwable cause) {
        super(message, cause);
        this.symbol = symbol;
        this.operation = operation;
        this.provider = provider;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getOperation() {
        return operation;
    }

    public String getProvider() {
        return provider;
    }

    /**
     * Creates a formatted error message with context
     */
    public String getDetailedMessage() {
        StringBuilder sb = new StringBuilder(getMessage());

        if (symbol != null) {
            sb.append(" [Symbol: ").append(symbol).append("]");
        }

        if (operation != null) {
            sb.append(" [Operation: ").append(operation).append("]");
        }

        if (provider != null) {
            sb.append(" [Provider: ").append(provider).append("]");
        }

        return sb.toString();
    }

    /**
     * Factory method for price fetch failures
     */
    public static MarketDataException priceNotAvailable(String symbol) {
        return new MarketDataException(
                "Price data not available for symbol: " + symbol,
                symbol,
                "getCurrentPrice",
                "market_data_api"
        );
    }

    /**
     * Factory method for API rate limit exceeded
     */
    public static MarketDataException rateLimitExceeded(String provider) {
        return new MarketDataException(
                "API rate limit exceeded for provider: " + provider,
                null,
                null,
                provider
        );
    }

    /**
     * Factory method for invalid symbol
     */
    public static MarketDataException invalidSymbol(String symbol) {
        return new MarketDataException(
                "Invalid or unsupported symbol: " + symbol,
                symbol,
                "validateSymbol",
                null
        );
    }

    /**
     * Factory method for API connection failures
     */
    public static MarketDataException connectionFailure(String provider, Throwable cause) {
        return new MarketDataException(
                "Failed to connect to market data provider: " + provider,
                null,
                "api_connection",
                provider,
                cause
        );
    }

    /**
     * Factory method for data parsing errors
     */
    public static MarketDataException dataParsingError(String symbol, String operation, Throwable cause) {
        return new MarketDataException(
                "Failed to parse market data response",
                symbol,
                operation,
                null,
                cause
        );
    }
}