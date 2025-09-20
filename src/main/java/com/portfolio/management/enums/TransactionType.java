package com.portfolio.management.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TransactionType {
    BUY("buy", "Buy"),
    SELL("sell", "Sell"),
    DIVIDEND("dividend", "Dividend");

    private final String code;
    private final String displayName;

    TransactionType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static TransactionType fromCode(String code) {
        for (TransactionType type : TransactionType.values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown transaction type: " + code);
    }

    public static TransactionType fromString(String value) {
        return fromCode(value);
    }

    public boolean isBuy() {
        return this == BUY;
    }

    public boolean isSell() {
        return this == SELL;
    }

    public boolean isDividend() {
        return this == DIVIDEND;
    }

    @Override
    public String toString() {
        return code;
    }
}
