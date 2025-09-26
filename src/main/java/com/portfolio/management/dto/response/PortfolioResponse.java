package com.portfolio.management.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.portfolio.management.enums.PortfolioStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PortfolioResponse {
    private String id;
    private String name;
    private String description;
    private String currency;
    private boolean isPublic;
    private PortfolioStatus status;
    private String userId;
    private String userName;

    // Financial Data
    private BigDecimal totalValue;
    private BigDecimal totalInvested;
    private BigDecimal totalGainLoss;
    private BigDecimal gainLossPercentage;
    private BigDecimal dayChange;
    private BigDecimal dayChangePercentage;
    private BigDecimal totalDividends;

    // Statistics
    private Integer holdingsCount;
    private Integer transactionsCount;

    // Timestamps
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant updatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant lastModifiedAt;

    // Top Holdings (optional)
    private List<HoldingResponse> topHoldings;

    // Recent Transactions (optional)
    private List<TransactionResponse> recentTransactions;

    // Constructors
    public PortfolioResponse() {
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private PortfolioResponse response = new PortfolioResponse();

        public Builder id(String id) {
            response.id = id;
            return this;
        }

        public Builder name(String name) {
            response.name = name;
            return this;
        }

        public Builder description(String description) {
            response.description = description;
            return this;
        }

        public Builder currency(String currency) {
            response.currency = currency;
            return this;
        }

        public Builder isPublic(boolean isPublic) {
            response.isPublic = isPublic;
            return this;
        }

        public Builder status(PortfolioStatus status) {
            response.status = status;
            return this;
        }

        public Builder userId(String userId) {
            response.userId = userId;
            return this;
        }

        public Builder userName(String userName) {
            response.userName = userName;
            return this;
        }

        public Builder totalValue(BigDecimal totalValue) {
            response.totalValue = totalValue;
            return this;
        }

        public Builder totalInvested(BigDecimal totalInvested) {
            response.totalInvested = totalInvested;
            return this;
        }

        public Builder totalGainLoss(BigDecimal totalGainLoss) {
            response.totalGainLoss = totalGainLoss;
            return this;
        }

        public Builder gainLossPercentage(BigDecimal gainLossPercentage) {
            response.gainLossPercentage = gainLossPercentage;
            return this;
        }

        public Builder dayChange(BigDecimal dayChange) {
            response.dayChange = dayChange;
            return this;
        }

        public Builder dayChangePercentage(BigDecimal dayChangePercentage) {
            response.dayChangePercentage = dayChangePercentage;
            return this;
        }

        public Builder totalDividends(BigDecimal totalDividends) {
            response.totalDividends = totalDividends;
            return this;
        }

        public Builder holdingsCount(Integer holdingsCount) {
            response.holdingsCount = holdingsCount;
            return this;
        }

        public Builder transactionsCount(Integer transactionsCount) {
            response.transactionsCount = transactionsCount;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            response.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            response.updatedAt = updatedAt;
            return this;
        }

        public Builder lastModifiedAt(Instant lastModifiedAt) {
            response.lastModifiedAt = lastModifiedAt;
            return this;
        }

        public Builder topHoldings(List<HoldingResponse> topHoldings) {
            response.topHoldings = topHoldings;
            return this;
        }

        public Builder recentTransactions(List<TransactionResponse> recentTransactions) {
            response.recentTransactions = recentTransactions;
            return this;
        }

        public PortfolioResponse build() {
            return response;
        }
    }

    // Getters and setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public PortfolioStatus getStatus() {
        return status;
    }

    public void setStatus(PortfolioStatus status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    public BigDecimal getTotalInvested() {
        return totalInvested;
    }

    public void setTotalInvested(BigDecimal totalInvested) {
        this.totalInvested = totalInvested;
    }

    public BigDecimal getTotalGainLoss() {
        return totalGainLoss;
    }

    public void setTotalGainLoss(BigDecimal totalGainLoss) {
        this.totalGainLoss = totalGainLoss;
    }

    public BigDecimal getGainLossPercentage() {
        return gainLossPercentage;
    }

    public void setGainLossPercentage(BigDecimal gainLossPercentage) {
        this.gainLossPercentage = gainLossPercentage;
    }

    public BigDecimal getDayChange() {
        return dayChange;
    }

    public void setDayChange(BigDecimal dayChange) {
        this.dayChange = dayChange;
    }

    public BigDecimal getDayChangePercentage() {
        return dayChangePercentage;
    }

    public void setDayChangePercentage(BigDecimal dayChangePercentage) {
        this.dayChangePercentage = dayChangePercentage;
    }

    public BigDecimal getTotalDividends() {
        return totalDividends;
    }

    public void setTotalDividends(BigDecimal totalDividends) {
        this.totalDividends = totalDividends;
    }

    public Integer getHoldingsCount() {
        return holdingsCount;
    }

    public void setHoldingsCount(Integer holdingsCount) {
        this.holdingsCount = holdingsCount;
    }

    public Integer getTransactionsCount() {
        return transactionsCount;
    }

    public void setTransactionsCount(Integer transactionsCount) {
        this.transactionsCount = transactionsCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getLastModifiedAt() {
        return lastModifiedAt;
    }

    public void setLastModifiedAt(Instant lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    public List<HoldingResponse> getTopHoldings() {
        return topHoldings;
    }

    public void setTopHoldings(List<HoldingResponse> topHoldings) {
        this.topHoldings = topHoldings;
    }

    public List<TransactionResponse> getRecentTransactions() {
        return recentTransactions;
    }

    public void setRecentTransactions(List<TransactionResponse> recentTransactions) {
        this.recentTransactions = recentTransactions;
    }
}
