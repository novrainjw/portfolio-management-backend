package com.portfolio.management.exception;

/**
 * Exception thrown when trying to sell more shares than available in a holding
 */
public class InsufficientQuantityException extends RuntimeException {

    private final String symbol;
    private final java.math.BigDecimal availableQuantity;
    private final java.math.BigDecimal requestedQuantity;

    public InsufficientQuantityException(String message) {
        super(message);
        this.symbol = null;
        this.availableQuantity = null;
        this.requestedQuantity = null;
    }

    public InsufficientQuantityException(String message, Throwable cause) {
        super(message, cause);
        this.symbol = null;
        this.availableQuantity = null;
        this.requestedQuantity = null;
    }

    public InsufficientQuantityException(String symbol, java.math.BigDecimal availableQuantity, java.math.BigDecimal requestedQuantity) {
        super(String.format("Insufficient quantity for %s. Available: %s, Requested: %s",
                symbol, availableQuantity, requestedQuantity));
        this.symbol = symbol;
        this.availableQuantity = availableQuantity;
        this.requestedQuantity = requestedQuantity;
    }

    public InsufficientQuantityException(String message, String symbol, java.math.BigDecimal availableQuantity, java.math.BigDecimal requestedQuantity) {
        super(message);
        this.symbol = symbol;
        this.availableQuantity = availableQuantity;
        this.requestedQuantity = requestedQuantity;
    }

    public String getSymbol() {
        return symbol;
    }

    public java.math.BigDecimal getAvailableQuantity() {
        return availableQuantity;
    }

    public java.math.BigDecimal getRequestedQuantity() {
        return requestedQuantity;
    }
}