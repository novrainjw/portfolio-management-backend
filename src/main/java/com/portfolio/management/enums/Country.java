package com.portfolio.management.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Country {
    CANADA("Canada", "CA", "Canadian"),
    USA("USA", "US", "American"),
    UK("United Kingdom", "GB", "British"),
    GERMANY("Germany", "DE", "German"),
    FRANCE("France", "FR", "French"),
    JAPAN("Japan", "JP", "Japanese"),
    AUSTRALIA("Australia", "AU", "Australian");

    private final String name;
    private final String code;
    private final String nationality;

    Country(String name, String code, String nationality) {
        this.name = name;
        this.code = code;
        this.nationality = nationality;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getNationality() {
        return nationality;
    }

    @JsonCreator
    public static Country fromName(String name) {
        for (Country country : Country.values()) {
            if (country.name.equalsIgnoreCase(name) || country.code.equalsIgnoreCase(name)) {
                return country;
            }
        }
        throw new IllegalArgumentException("Unknown country: " + name);
    }

    public static Country fromString(String value) {
        return fromName(value);
    }

    public boolean isCanada() {
        return this == CANADA;
    }

    public boolean isUSA() {
        return this == USA;
    }

    public boolean isUK() {
        return this == UK;
    }

    @Override
    public String toString() {
        return name;
    }
}
