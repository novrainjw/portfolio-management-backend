package com.portfolio.management.entity;

import com.portfolio.management.enums.Currency;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.UuidGenerator;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "portfolios", indexes = {
        @Index(name = "idx_portfolios_user_id", columnList = "user_id"),
        @Index(name = "idx_portfolios_broker", columnList = "broker"),
        @Index(name = "idx_portfolios_currency", columnList = "currency"),
        @Index(name = "idx_portfolios_created_at", columnList = "created_at"),
        @Index(name = "idx_portfolios_updated_at", columnList = "updated_at")
},
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_portfolio_name", columnNames = {"user_id", "name"})
        })
public class Portfolio extends BaseEntity {

    @Id
    @UuidGenerator
    @Column(name = "id", length = 50)
    private String id;

    @NotBlank(message = "Portfolio name is required")
    @Size(min = 2, max = 100, message = "Portfolio name must be between 2 and 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_portfolio_user"))
    private User user;

    @NotBlank(message = "Broker is required")
    @Size(max = 100, message = "Broker name cannot exceed 100 characters")
    @Column(name = "broker", nullable = false, length = 100)
    private String broker;

    @NotNull(message = "Currency is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, length = 3)
    private Currency currency;

    @Column(name = "total_value", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalValue = BigDecimal.ZERO;

    @Column(name = "total_cost", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalCost = BigDecimal.ZERO;

    @Column(name = "total_gain_loss", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalGainLoss = BigDecimal.ZERO;

    @Column(name = "total_gain_loss_percent", precision = 8, scale = 4, nullable = false)
    private BigDecimal totalGainLossPercent = BigDecimal.ZERO;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Holding> holdings = new HashSet<>();

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Transaction> transactions = new HashSet<>();

    // Constructors
    public Portfolio() {
    }

    public Portfolio(String name, String description, User user, String broker, Currency currency) {
        this.name = name;
        this.description = description;
        this.user = user;
        this.broker = broker;
        this.currency = currency;
    }

    // Business Methods
    public boolean isPortfolioActive() {
        return Boolean.TRUE.equals(isActive);
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public BigDecimal getGainLossAmount() {
        return totalValue.subtract(totalCost);
    }

    public boolean hasPositivePerformance() {
        return totalGainLossPercent.compareTo(BigDecimal.ZERO) > 0;
    }

    public int getHoldingsCount() {
        return holdings.size();
    }

    public void recalculateTotals() {
        this.totalValue = holdings.stream()
                .map(Holding::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.totalCost = holdings.stream()
                .map(Holding::getTotalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.totalGainLoss = totalValue.subtract(totalCost);

        if (totalCost.compareTo(BigDecimal.ZERO) > 0) {
            this.totalGainLossPercent = totalGainLoss
                    .divide(totalCost, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        } else {
            this.totalGainLossPercent = BigDecimal.ZERO;
        }
    }

    // Helper methods for bidirectional relationships
    public void addHolding(Holding holding) {
        holdings.add(holding);
        holding.setPortfolio(this);
        recalculateTotals();
    }

    public void removeHolding(Holding holding) {
        holdings.remove(holding);
        holding.setPortfolio(null);
        recalculateTotals();
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        transaction.setPortfolio(this);
    }

    public void removeTransaction(Transaction transaction) {
        transactions.remove(transaction);
        transaction.setPortfolio(null);
    }

    @Override
    public String getId() {
        return "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBroker() {
        return broker;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public BigDecimal getTotalGainLoss() {
        return totalGainLoss;
    }

    public void setTotalGainLoss(BigDecimal totalGainLoss) {
        this.totalGainLoss = totalGainLoss;
    }

    public BigDecimal getTotalGainLossPercent() {
        return totalGainLossPercent;
    }

    public void setTotalGainLossPercent(BigDecimal totalGainLossPercent) {
        this.totalGainLossPercent = totalGainLossPercent;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Set<Holding> getHoldings() {
        return holdings;
    }

    public void setHoldings(Set<Holding> holdings) {
        this.holdings = holdings;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }
}
