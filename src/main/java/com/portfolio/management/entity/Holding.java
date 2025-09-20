package com.portfolio.management.entity;

import com.portfolio.management.enums.Currency;
import com.portfolio.management.enums.HoldingType;
import com.portfolio.management.enums.Sector;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.UuidGenerator;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "holdings", indexes = {
        @Index(name = "idx_holdings_portfolio_id", columnList = "portfolio_id"),
        @Index(name = "idx_holdings_symbol", columnList = "symbol"),
        @Index(name = "idx_holdings_type", columnList = "type"),
        @Index(name = "idx_holdings_sector", columnList = "sector"),
        @Index(name = "idx_holdings_last_updated", columnList = "last_updated")
},
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_portfolio_symbol", columnNames = {"portfolio_id", "symbol"})})
public class Holding extends BaseEntity {
    @Id
    @UuidGenerator
    @Column(name = "id", length = 50)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "portfolio_id", nullable = false, foreignKey = @ForeignKey(name = "fk_holding_portfolio"))
    private Portfolio portfolio;

    @NotBlank(message = "Symbol is required")
    @Size(min = 1, max = 20, message = "Symbol must be between 1 and 20 characters")
    @Pattern(regexp = "^[A-Z0-9._-]+$", message = "Symbol must contain only uppercase letters, numbers, dots, underscores, and hyphens")
    @Column(name = "symbol", nullable = false, length = 20)
    private String symbol;

    @NotBlank(message = "Company name is required")
    @Size(min = 1, max = 200, message = "Company name must be between 1 and 200 characters")
    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName;

    @NotNull(message = "Holding type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private HoldingType type;

    @NotBlank(message = "Market is required")
    @Size(max = 50, message = "Market name cannot exceed 50 characters")
    @Column(name = "market", nullable = false, length = 50)
    private String market;

    @NotNull(message = "Currency is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, length = 3)
    private Currency currency;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.000001", message = "Quantity must be greater than 0")
    @Digits(integer = 15, fraction = 6, message = "Quantity must have at most 15 integer digits and 6 decimal places")
    @Column(name = "quantity", precision = 15, scale = 6, nullable = false)
    private BigDecimal quantity;

    @NotNull(message = "Average price is required")
    @DecimalMin(value = "0.0001", message = "Average price must be greater than 0")
    @Digits(integer = 15, fraction = 4, message = "Average price must have at most 15 integer digits and 4 decimal places")
    @Column(name = "average_price", precision = 15, scale = 4, nullable = false)
    private BigDecimal averagePrice;

    @NotNull(message = "Current price is required")
    @DecimalMin(value = "0.0000", message = "Current price must be greater than or equal to 0")
    @Digits(integer = 15, fraction = 4, message = "Current price must have at most 15 integer digits and 4 decimal places")
    @Column(name = "current_price", precision = 15, scale = 4, nullable = false)
    private BigDecimal currentPrice;

    @Column(name = "total_cost", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalCost = BigDecimal.ZERO;

    @Column(name = "current_value", precision = 15, scale = 2, nullable = false)
    private BigDecimal currentValue = BigDecimal.ZERO;

    @Column(name = "gain_loss", precision = 15, scale = 2, nullable = false)
    private BigDecimal gainLoss = BigDecimal.ZERO;

    @Column(name = "gain_loss_percent", precision = 8, scale = 4, nullable = false)
    private BigDecimal gainLossPercent = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "sector", length = 100)
    private Sector sector;

    @NotNull(message = "Purchase date is required")
    @Column(name = "purchase_date", nullable = false)
    private Instant purchaseDate;

    @Column(name = "last_updated")
    private Instant lastUpdated;

    @OneToMany(mappedBy = "holding", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Transaction> transactions = new HashSet<>();

    // Constructors
    public Holding() {
    }

    public Holding(Portfolio portfolio, String symbol, String companyName, HoldingType type,
                   String market, Currency currency, BigDecimal quantity, BigDecimal averagePrice,
                   BigDecimal currentPrice, Sector sector, Instant purchaseDate) {
        this.portfolio = portfolio;
        this.symbol = symbol;
        this.companyName = companyName;
        this.type = type;
        this.market = market;
        this.currency = currency;
        this.quantity = quantity;
        this.averagePrice = averagePrice;
        this.currentPrice = currentPrice;
        this.sector = sector;
        this.purchaseDate = purchaseDate;
        recalculateValues();
    }

    // Business Methods
    public void recalculateValues() {
        this.totalCost = quantity.multiply(averagePrice);
        this.currentValue = quantity.multiply(currentPrice);
        this.gainLoss = currentValue.subtract(totalCost);

        if (totalCost.compareTo(BigDecimal.ZERO) > 0) {
            this.gainLossPercent = gainLoss
                    .divide(totalCost, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        } else {
            this.gainLossPercent = BigDecimal.ZERO;
        }

        this.lastUpdated = Instant.now();
    }

    public void updateCurrentPrice(BigDecimal newPrice) {
        this.currentPrice = newPrice;
        recalculateValues();
    }

    public boolean hasPositivePerformance() {
        return gainLossPercent.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isStock() {
        return HoldingType.STOCK.equals(type);
    }

    public boolean isETF() {
        return HoldingType.ETF.equals(type);
    }

    public String getDisplaySymbol() {
        return symbol.toUpperCase();
    }

    // Helper methods for bidirectional relationships
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        transaction.setHolding(this);
    }

    public void removeTransaction(Transaction transaction) {
        transactions.remove(transaction);
        transaction.setHolding(null);
    }

    @PrePersist
    @PreUpdate
    private void updateCalculatedFields() {
        recalculateValues();
    }

    // Getters and Setters
    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public HoldingType getType() {
        return type;
    }

    public void setType(HoldingType type) {
        this.type = type;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(BigDecimal averagePrice) {
        this.averagePrice = averagePrice;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public BigDecimal getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(BigDecimal currentValue) {
        this.currentValue = currentValue;
    }

    public BigDecimal getGainLoss() {
        return gainLoss;
    }

    public void setGainLoss(BigDecimal gainLoss) {
        this.gainLoss = gainLoss;
    }

    public BigDecimal getGainLossPercent() {
        return gainLossPercent;
    }

    public void setGainLossPercent(BigDecimal gainLossPercent) {
        this.gainLossPercent = gainLossPercent;
    }

    public Sector getSector() {
        return sector;
    }

    public void setSector(Sector sector) {
        this.sector = sector;
    }

    public Instant getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Instant purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }
}
