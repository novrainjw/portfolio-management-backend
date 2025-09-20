package com.portfolio.management.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BrokerType {
    DISCOUNT("discount", "Discount Broker"),
    FULL_SERVICE("full-service", "Full Service Broker"),
    PROFESSIONAL("professional", "Professional/Institutional"),
    COMMISSION_FREE("commission-free", "Commission-Free Broker"),
    ROBO_ADVISOR("robo-advisor", "Robo Advisor");

    private final String code;
    private final String displayName;

    BrokerType(String code, String displayName) {
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
    public static BrokerType fromCode(String code) {
        for (BrokerType type : BrokerType.values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown broker type: " + code);
    }

    public static BrokerType fromString(String value) {
        return fromCode(value);
    }

    public boolean isCommissionFree() {
        return this == COMMISSION_FREE;
    }

    public boolean isFullService() {
        return this == FULL_SERVICE;
    }

    public boolean isDiscount() {
        return this == DISCOUNT;
    }

    public boolean isProfessional() {
        return this == PROFESSIONAL;
    }

    public boolean isRoboAdvisor() {
        return this == ROBO_ADVISOR;
    }

    @Override
    public String toString() {
        return code;
    }
}
