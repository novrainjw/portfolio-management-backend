package com.portfolio.management.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.portfolio.management.enums.HoldingStatus;

import java.math.BigDecimal;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HoldingResponse {
    private String id;
    private String portfolioId;
    private String portfolioName;
    private String symbol;
    private String companyName;
    private String sector;
    private String industry;
    private String country;
    private String exchange;
    private HoldingStatus status;

    // Position Data
    private BigDecimal quantity;
    private BigDecimal averagePrice;
    private BigDecimal currentPrice;
    private BigDecimal previousClosePrice;
    private BigDecimal targetPrice;
    private BigDecimal stopLossPrice;

    // Financial Calculations
    private BigDecimal totalValue;
    private BigDecimal totalInvested;
    private BigDecimal gainLoss;
    private BigDecimal gainLossPercentage;
    private BigDecimal dayChange;
    private BigDecimal dayChangePercentage;
    private BigDecimal totalDividends;
    private BigDecimal dividendYield;

    // Statistics
    private Integer transactionCount;
    private BigDecimal totalFees;
    private BigDecimal portfolioPercentage;

    // Price Targets
    private Boolean isAtTargetPrice;
    private Boolean isBelowStopLoss;
    private BigDecimal targetPriceDistance;
    private BigDecimal stopLossDistance;

    // Timestamps
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant purchaseDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant lastTransactionDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant priceLastUpdated;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant updatedAt;

    private String notes;

    // Constructors
    public HoldingResponse() {
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private HoldingResponse response = new HoldingResponse();

        public Builder id(String id) {
            response.id = id;
            return this;
        }

        public Builder portfolioId(String portfolioId) {
            response.portfolioId = portfolioId;
            return this;
        }

        public Builder portfolioName(String portfolioName) {
            response.portfolioName = portfolioName;
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

        public Builder sector(String sector) {
            response.sector = sector;
            return this;
        }

        public Builder industry(String industry) {
            response.industry = industry;
            return this;
        }

        public Builder country(String country) {
            response.country = country;
            return this;
        }

        public Builder exchange(String exchange) {
            response.exchange = exchange;
            return this;
        }

        public Builder status(HoldingStatus status) {
            response.status = status;
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

        public Builder previousClosePrice(BigDecimal previousClosePrice) {
            response.previousClosePrice = previousClosePrice;
            return this;
        }

        public Builder targetPrice(BigDecimal targetPrice) {
            response.targetPrice = targetPrice;
            return this;
        }

        public Builder stopLossPrice(BigDecimal stopLossPrice) {
            response.stopLossPrice = stopLossPrice;
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

        public Builder totalDividends(BigDecimal totalDividends) {
            response.totalDividends = totalDividends;
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

        public Builder portfolioPercentage(BigDecimal portfolioPercentage) {
            response.portfolioPercentage = portfolioPercentage;
            return this;
        }

        public Builder isAtTargetPrice(Boolean isAtTargetPrice) {
            response.isAtTargetPrice = isAtTargetPrice;
            return this;
        }

        public Builder isBelowStopLoss(Boolean isBelowStopLoss) {
            response.isBelowStopLoss = isBelowStopLoss;
            return this;
        }

        public Builder targetPriceDistance(BigDecimal targetPriceDistance) {
            response.targetPriceDistance = targetPriceDistance;
            return this;
        }

        public Builder stopLossDistance(BigDecimal stopLossDistance) {
            response.stopLossDistance = stopLossDistance;
            return this;
        }

        public Builder purchaseDate(Instant purchaseDate) {
            response.purchaseDate = purchaseDate;
            return this;
        }

        public Builder lastTransactionDate(Instant lastTransactionDate) {
            response.lastTransactionDate = lastTransactionDate;
            return this;
        }

        public Builder priceLastUpdated(Instant priceLastUpdated) {
            response.priceLastUpdated = priceLastUpdated;
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

        public Builder notes(String notes) {
            response.notes = notes;
            return this;
        }

        public HoldingResponse build() {
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

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
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

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public HoldingStatus getStatus() {
        return status;
    }

    public void setStatus(HoldingStatus status) {
        this.status = status;
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

    public BigDecimal getPreviousClosePrice() {
        return previousClosePrice;
    }

    public void setPreviousClosePrice(BigDecimal previousClosePrice) {
        this.previousClosePrice = previousClosePrice;
    }

    public BigDecimal getTargetPrice() {
        return targetPrice;
    }

    public void setTargetPrice(BigDecimal targetPrice) {
        this.targetPrice = targetPrice;
    }

    public BigDecimal getStopLossPrice() {
        return stopLossPrice;
    }

    public void setStopLossPrice(BigDecimal stopLossPrice) {
        this.stopLossPrice = stopLossPrice;
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

    public BigDecimal getTotalDividends() {
        return totalDividends;
    }

    public void setTotalDividends(BigDecimal totalDividends) {
        this.totalDividends = totalDividends;
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

    public BigDecimal getPortfolioPercentage() {
        return portfolioPercentage;
    }

    public void setPortfolioPercentage(BigDecimal portfolioPercentage) {
        this.portfolioPercentage = portfolioPercentage;
    }

    public Boolean getIsAtTargetPrice() {
        return isAtTargetPrice;
    }

    public void setIsAtTargetPrice(Boolean isAtTargetPrice) {
        this.isAtTargetPrice = isAtTargetPrice;
    }

    public Boolean getIsBelowStopLoss() {
        return isBelowStopLoss;
    }

    public void setIsBelowStopLoss(Boolean isBelowStopLoss) {
        this.isBelowStopLoss = isBelowStopLoss;
    }

    public BigDecimal getTargetPriceDistance() {
        return targetPriceDistance;
    }

    public void setTargetPriceDistance(BigDecimal targetPriceDistance) {
        this.targetPriceDistance = targetPriceDistance;
    }

    public BigDecimal getStopLossDistance() {
        return stopLossDistance;
    }

    public void setStopLossDistance(BigDecimal stopLossDistance) {
        this.stopLossDistance = stopLossDistance;
    }

    public Instant getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Instant purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Instant getLastTransactionDate() {
        return lastTransactionDate;
    }

    public void setLastTransactionDate(Instant lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }

    public Instant getPriceLastUpdated() {
        return priceLastUpdated;
    }

    public void setPriceLastUpdated(Instant priceLastUpdated) {
        this.priceLastUpdated = priceLastUpdated;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
