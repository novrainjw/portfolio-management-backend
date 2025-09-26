package com.portfolio.management.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HoldingSummaryResponse {
    private String holdingId;
    private String symbol;
    private String companyName;
    private String portfolioName;

    // Key Metrics
    private BigDecimal quantity;
    private BigDecimal averagePrice;
    private BigDecimal currentPrice;
    private BigDecimal totalValue;
    private BigDecimal totalInvested;
    private BigDecimal gainLoss;
    private BigDecimal gainLossPercentage;
    private BigDecimal dayChange;
    private BigDecimal dayChangePercentage;
    private BigDecimal portfolioPercentage;
    private BigDecimal dividendYield;

    // Statistics
    private Integer transactionCount;
    private BigDecimal totalFees;
    private BigDecimal totalDividends;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant purchaseDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant lastUpdated;

    // Constructors
    public HoldingSummaryResponse() {
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private HoldingSummaryResponse response = new HoldingSummaryResponse();

        public Builder holdingId(String holdingId) {
            response.holdingId = holdingId;
            return this;
        }

        public Builder symbol(String symbol) {
            response.symbol = symbol;
            return this;
        }

        public Builder companyName(String companyName) {
            response.companyName = companyName;
            return this;
        }

        public Builder portfolioName(String portfolioName) {
            response.portfolioName = portfolioName;
            return this;
        }

        public Builder quantity(BigDecimal quantity) {
            response.quantity = quantity;
            return this;
        }

        public Builder averagePrice(BigDecimal averagePrice) {
            response.averagePrice = averagePrice;
            return this;
        }

        public Builder currentPrice(BigDecimal currentPrice) {
            response.currentPrice = currentPrice;
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

        public Builder gainLoss(BigDecimal gainLoss) {
            response.gainLoss = gainLoss;
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

        public Builder portfolioPercentage(BigDecimal portfolioPercentage) {
            response.portfolioPercentage = portfolioPercentage;
            return this;
        }

        public Builder dividendYield(BigDecimal dividendYield) {
            response.dividendYield = dividendYield;
            return this;
        }

        public Builder transactionCount(Integer transactionCount) {
            response.transactionCount = transactionCount;
            return this;
        }

        public Builder totalFees(BigDecimal totalFees) {
            response.totalFees = totalFees;
            return this;
        }

        public Builder totalDividends(BigDecimal totalDividends) {
            response.totalDividends = totalDividends;
            return this;
        }

        public Builder purchaseDate(Instant purchaseDate) {
            response.purchaseDate = purchaseDate;
            return this;
        }

        public Builder lastUpdated(Instant lastUpdated) {
            response.lastUpdated = lastUpdated;
            return this;
        }

        public HoldingSummaryResponse build() {
            return response;
        }
    }

    // Getters and setters
    public String getHoldingId() {
        return holdingId;
    }

    public void setHoldingId(String holdingId) {
        this.holdingId = holdingId;
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

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
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

    public BigDecimal getGainLoss() {
        return gainLoss;
    }

    public void setGainLoss(BigDecimal gainLoss) {
        this.gainLoss = gainLoss;
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

    public BigDecimal getPortfolioPercentage() {
        return portfolioPercentage;
    }

    public void setPortfolioPercentage(BigDecimal portfolioPercentage) {
        this.portfolioPercentage = portfolioPercentage;
    }

    public BigDecimal getDividendYield() {
        return dividendYield;
    }

    public void setDividendYield(BigDecimal dividendYield) {
        this.dividendYield = dividendYield;
    }

    public Integer getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(Integer transactionCount) {
        this.transactionCount = transactionCount;
    }

    public BigDecimal getTotalFees() {
        return totalFees;
    }

    public void setTotalFees(BigDecimal totalFees) {
        this.totalFees = totalFees;
    }

    public BigDecimal getTotalDividends() {
        return totalDividends;
    }

    public void setTotalDividends(BigDecimal totalDividends) {
        this.totalDividends = totalDividends;
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
}
