package com.portfolio.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "watchlist",
        indexes = {
                @Index(name = "idx_watchlist_user_id", columnList = "user_id"),
                @Index(name = "idx_watchlist_symbol", columnList = "symbol"),
                @Index(name = "idx_watchlist_added_date", columnList = "added_date")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_watchlist_symbol", columnNames = {"user_id", "symbol"})
        })
public class Watchlist extends BaseEntity {
    @Id
    @UuidGenerator
    @Column(name = "id", length = 50)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_watchlist_user"))
    private User user;

    @NotBlank(message = "Symbol is required")
    @Size(min = 1, max = 20, message = "Symbol must be between 1 and 20 characters")
    @Pattern(regexp = "^[A-Z0-9._-]+$", message = "Symbol must contain only uppercase letters, numbers, dots, underscores, and hyphens")
    @Column(name = "symbol", nullable = false, length = 20)
    private String symbol;

    @NotBlank(message = "Company name is required")
    @Size(min = 1, max = 200, message = "Company name must be between 1 and 200 characters")
    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName;

    @Column(name = "current_price", precision = 15, scale = 4, nullable = false)
    private BigDecimal currentPrice = BigDecimal.ZERO;

    @Column(name = "change_percent", precision = 8, scale = 4, nullable = false)
    private BigDecimal changePercent = BigDecimal.ZERO;

    @Column(name = "added_date", nullable = false)
    private Instant addedDate;

    // Constructors
    public Watchlist() {
    }

    public Watchlist(User user, String symbol, String companyName, BigDecimal currentPrice,
                     BigDecimal changePercent) {
        this.user = user;
        this.symbol = symbol;
        this.companyName = companyName;
        this.currentPrice = currentPrice;
        this.changePercent = changePercent;
        this.addedDate = Instant.now();
    }

    // Business Methods
    public boolean hasPositiveChange() {
        return changePercent.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasNegativeChange() {
        return changePercent.compareTo(BigDecimal.ZERO) < 0;
    }

    public String getDisplaySymbol() {
        return symbol.toUpperCase();
    }

    public void updatePrice(BigDecimal newPrice, BigDecimal newChangePercent) {
        this.currentPrice = newPrice;
        this.changePercent = newChangePercent;
    }

    @PrePersist
    private void setAddedDate() {
        if (addedDate == null) {
            addedDate = Instant.now();
        }
    }

    // Getters and Setters
    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public BigDecimal getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(BigDecimal changePercent) {
        this.changePercent = changePercent;
    }

    public Instant getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Instant addedDate) {
        this.addedDate = addedDate;
    }
}
