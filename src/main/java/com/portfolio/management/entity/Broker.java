package com.portfolio.management.entity;

import com.portfolio.management.enums.BrokerType;
import com.portfolio.management.enums.Country;
import com.portfolio.management.enums.Currency;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "brokers")
public class Broker extends BaseEntity {
    @Id
    @UuidGenerator
    @Column(name = "id", length = 50)
    private String id;

    @NotBlank(message = "Broker name is required")
    @Size(min = 2, max = 100, message = "Broker name must be between 2 and 100 characters")
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @NotNull(message = "Broker type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private BrokerType type;

    @NotNull(message = "Country is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "country", nullable = false, length = 50)
    private Country country;

    @NotEmpty(message = "At least one supported currency is required")
    @ElementCollection(targetClass = Currency.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "broker_supported_currencies",
            joinColumns = @JoinColumn(name = "broker_id"),
            foreignKey = @ForeignKey(name = "fk_broker_currencies"))
    @Column(name = "currency", length = 3)
    private Set<Currency> supportedCurrencies = new HashSet<>();

    @Column(name = "trading_fee", precision = 10, scale = 2, nullable = false)
    private BigDecimal tradingFee = BigDecimal.ZERO;

    @Column(name = "min_trading_fee", precision = 10, scale = 2, nullable = false)
    private BigDecimal minTradingFee = BigDecimal.ZERO;

    @Column(name = "max_trading_fee", precision = 10, scale = 2, nullable = false)
    private BigDecimal maxTradingFee = BigDecimal.ZERO;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "website_url", length = 255)
    private String websiteUrl;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Constructors
    public Broker() {}

    public Broker(String name, BrokerType type, Country country, Set<Currency> supportedCurrencies,
                  BigDecimal tradingFee, BigDecimal minTradingFee, BigDecimal maxTradingFee) {
        this.name = name;
        this.type = type;
        this.country = country;
        this.supportedCurrencies = supportedCurrencies;
        this.tradingFee = tradingFee;
        this.minTradingFee = minTradingFee;
        this.maxTradingFee = maxTradingFee;
    }

    // Business Methods
    public boolean isActive() {
        return Boolean.TRUE.equals(isActive);
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public boolean supportsCurrency(Currency currency) {
        return supportedCurrencies.contains(currency);
    }

    public boolean isCommissionFree() {
        return BrokerType.COMMISSION_FREE.equals(type);
    }

    public boolean isCanadianBroker() {
        return Country.CANADA.equals(country);
    }

    public boolean isUSBroker() {
        return Country.USA.equals(country);
    }

    public BigDecimal calculateTradingFee(BigDecimal orderValue) {
        if (isCommissionFree()) {
            return BigDecimal.ZERO;
        }

        BigDecimal calculatedFee = tradingFee;

        // Apply min/max limits if they are set and greater than zero
        if (minTradingFee.compareTo(BigDecimal.ZERO) > 0 &&
                calculatedFee.compareTo(minTradingFee) < 0) {
            calculatedFee = minTradingFee;
        }

        if (maxTradingFee.compareTo(BigDecimal.ZERO) > 0 &&
                calculatedFee.compareTo(maxTradingFee) > 0) {
            calculatedFee = maxTradingFee;
        }

        return calculatedFee;
    }

    // Helper methods for supported currencies
    public void addSupportedCurrency(Currency currency) {
        supportedCurrencies.add(currency);
    }

    public void removeSupportedCurrency(Currency currency) {
        supportedCurrencies.remove(currency);
    }

    // Getters and Setters
    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BrokerType getType() {
        return type;
    }

    public void setType(BrokerType type) {
        this.type = type;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Set<Currency> getSupportedCurrencies() {
        return supportedCurrencies;
    }

    public void setSupportedCurrencies(Set<Currency> supportedCurrencies) {
        this.supportedCurrencies = supportedCurrencies;
    }

    public BigDecimal getTradingFee() {
        return tradingFee;
    }

    public void setTradingFee(BigDecimal tradingFee) {
        this.tradingFee = tradingFee;
    }

    public BigDecimal getMinTradingFee() {
        return minTradingFee;
    }

    public void setMinTradingFee(BigDecimal minTradingFee) {
        this.minTradingFee = minTradingFee;
    }

    public BigDecimal getMaxTradingFee() {
        return maxTradingFee;
    }

    public void setMaxTradingFee(BigDecimal maxTradingFee) {
        this.maxTradingFee = maxTradingFee;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
