package com.portfolio.management.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum HoldingType {
    STOCK("stock", "Stock"),
    ETF("etf", "Exchange Traded Fund"),
    MUTUAL_FUND("mutual_fund", "Mutual Fund"),
    BOND("bond", "Bond"),
    OPTION("option", "Option"),
    CRYPTO("crypto", "Cryptocurrency");

    private final String code;
    private final String displayName;

    HoldingType(String code, String displayName) {
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
    public static HoldingType fromCode(String code) {
        for (HoldingType type : HoldingType.values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown holding type: " + code);
    }

    public static HoldingType fromString(String value) {
        return fromCode(value);
    }

    public boolean isStock() {
        return this == STOCK;
    }

    public boolean isETF() {
        return this == ETF;
    }

    public boolean isMutualFund() {
        return this == MUTUAL_FUND;
    }

    public boolean isBond() {
        return this == BOND;
    }

    public boolean isOption() {
        return this == OPTION;
    }

    public boolean isCrypto() {
        return this == CRYPTO;
    }

    @Override
    public String toString() {
        return code;
    }
}
