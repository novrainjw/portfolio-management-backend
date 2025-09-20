package com.portfolio.management.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Currency {
    USD("USD", "US Dollar", "$"),
    CAD("CAD", "Canadian Dollar", "CA$"),
    EUR("EUR", "Euro", "€"),
    GBP("GBP", "British Pound", "£");

    private final String code;
    private final String displayName;
    private final String symbol;

    Currency(String code, String displayName, String symbol) {
        this.code = code;
        this.displayName = displayName;
        this.symbol = symbol;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSymbol() {
        return symbol;
    }

    @JsonCreator
    public static Currency fromCode(String code) {
        for (Currency currency : Currency.values()) {
            if (currency.code.equalsIgnoreCase(code)) {
                return currency;
            }
        }
        throw new IllegalArgumentException("Unknown currency code: " + code);
    }

    public static Currency fromString(String value) {
        return fromCode(value);
    }

    @Override
    public String toString() {
        return code;
    }
}
