package com.portfolio.management.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Sector {
    TECHNOLOGY("Technology", "Information Technology"),
    FINANCIAL_SERVICES("Financial Services", "Financial Services"),
    HEALTHCARE("Healthcare", "Healthcare"),
    CONSUMER_CYCLICAL("Consumer Cyclical", "Consumer Discretionary"),
    CONSUMER_DEFENSIVE("Consumer Defensive", "Consumer Staples"),
    ENERGY("Energy", "Energy"),
    INDUSTRIALS("Industrials", "Industrials"),
    MATERIALS("Materials", "Materials"),
    REAL_ESTATE("Real Estate", "Real Estate"),
    UTILITIES("Utilities", "Utilities"),
    COMMUNICATION_SERVICES("Communication Services", "Communication Services"),
    DIVERSIFIED("Diversified", "Diversified Holdings"),
    UNKNOWN("Unknown", "Unknown");

    private final String code;
    private final String displayName;

    Sector(String code, String displayName) {
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
    public static Sector fromCode(String code) {
        for (Sector sector : Sector.values()) {
            if (sector.code.equalsIgnoreCase(code) ||
                    sector.displayName.equalsIgnoreCase(code)) {
                return sector;
            }
        }
        // Return UNKNOWN instead of throwing exception for flexibility
        return UNKNOWN;
    }

    public static Sector fromString(String value) {
        return fromCode(value);
    }

    // Sector category checks
    public boolean isTechnology() {
        return this == TECHNOLOGY;
    }

    public boolean isFinancial() {
        return this == FINANCIAL_SERVICES;
    }

    public boolean isHealthcare() {
        return this == HEALTHCARE;
    }

    public boolean isConsumerCyclical() {
        return this == CONSUMER_CYCLICAL;
    }

    public boolean isConsumerDefensive() {
        return this == CONSUMER_DEFENSIVE;
    }

    public boolean isEnergy() {
        return this == ENERGY;
    }

    public boolean isIndustrials() {
        return this == INDUSTRIALS;
    }

    public boolean isMaterials() {
        return this == MATERIALS;
    }

    public boolean isRealEstate() {
        return this == REAL_ESTATE;
    }

    public boolean isUtilities() {
        return this == UTILITIES;
    }

    public boolean isCommunicationServices() {
        return this == COMMUNICATION_SERVICES;
    }

    public boolean isDiversified() {
        return this == DIVERSIFIED;
    }

    public boolean isUnknown() {
        return this == UNKNOWN;
    }

    // Sector grouping methods
    public boolean isGrowthSector() {
        return this == TECHNOLOGY || this == HEALTHCARE || this == CONSUMER_CYCLICAL;
    }

    public boolean isDefensiveSector() {
        return this == CONSUMER_DEFENSIVE || this == UTILITIES || this == HEALTHCARE;
    }

    public boolean isCyclicalSector() {
        return this == CONSUMER_CYCLICAL || this == INDUSTRIALS || this == MATERIALS || this == ENERGY;
    }

    public boolean isInterestSensitive() {
        return this == REAL_ESTATE || this == UTILITIES || this == FINANCIAL_SERVICES;
    }

    @Override
    public String toString() {
        return code;
    }
}
