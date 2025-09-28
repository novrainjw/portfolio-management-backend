package com.portfolio.management.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Response DTO for holding information with calculated financial metrics
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HoldingResponse {

    private String id;
    private String symbol;
    private String companyName;
    private BigDecimal quantity;
    private BigDecimal averagePrice;
    private BigDecimal currentPrice;
    private String portfolioId;
    private String portfolioName;
    private String status;

    // Price and target information
    private BigDecimal targetPrice;
    private BigDecimal stopLossPrice;
    private BigDecimal previousClosePrice;

    // Calculated financial metrics
    private BigDecimal totalValue;
    private BigDecimal totalInvested;
    private BigDecimal gainLoss;
    private BigDecimal gainLossPercentage;
    private BigDecimal dayChange;
    private BigDecimal dayChangePercentage;
    private BigDecimal portfolioPercentage;

    // Additional metrics
    private BigDecimal totalDividends;
    private Integer transactionCount;
    private BigDecimal totalFees;

    // Company and classification info
    private String sector;
    private String country;
    private String currency;

    // Alert and risk indicators
    private Boolean isAtTargetPrice;
    private Boolean isBelowStopLoss;
    private BigDecimal targetPriceDistance;
    private BigDecimal stopLossDistance;

    // Dates and tracking
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant purchaseDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant lastDividendDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant lastTransactionDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant priceLastUpdated;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant updatedAt;

    private String notes;

    // Constructors
    public HoldingResponse() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getAveragePrice() { return averagePrice; }
    public void setAveragePrice(BigDecimal averagePrice) { this.averagePrice = averagePrice; }

    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }

    public String getPortfolioId() { return portfolioId; }
    public void setPortfolioId(String portfolioId) { this.portfolioId = portfolioId; }

    public String getPortfolioName() { return portfolioName; }
    public void setPortfolioName(String portfolioName) { this.portfolioName = portfolioName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getTargetPrice() { return targetPrice; }
    public void setTargetPrice(BigDecimal targetPrice) { this.targetPrice = targetPrice; }

    public BigDecimal getStopLossPrice() { return stopLossPrice; }
    public void setStopLossPrice(BigDecimal stopLossPrice) { this.stopLossPrice = stopLossPrice; }

    public BigDecimal getPreviousClosePrice() { return previousClosePrice; }
    public void setPreviousClosePrice(BigDecimal previousClosePrice) { this.previousClosePrice = previousClosePrice; }

    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }

    public BigDecimal getTotalInvested() { return totalInvested; }
    public void setTotalInvested(BigDecimal totalInvested) { this.totalInvested = totalInvested; }

    public BigDecimal getGainLoss() { return gainLoss; }
    public void setGainLoss(BigDecimal gainLoss) { this.gainLoss = gainLoss; }

    public BigDecimal getGainLossPercentage() { return gainLossPercentage; }
    public void setGainLossPercentage(BigDecimal gainLossPercentage) { this.gainLossPercentage = gainLossPercentage; }

    public BigDecimal getDayChange() { return dayChange; }
    public void setDayChange(BigDecimal dayChange) { this.dayChange = dayChange; }

    public BigDecimal getDayChangePercentage() { return dayChangePercentage; }
    public void setDayChangePercentage(BigDecimal dayChangePercentage) { this.dayChangePercentage = dayChangePercentage; }

    public BigDecimal getPortfolioPercentage() { return portfolioPercentage; }
    public void setPortfolioPercentage(BigDecimal portfolioPercentage) { this.portfolioPercentage = portfolioPercentage; }

    public BigDecimal getTotalDividends() { return totalDividends; }
    public void setTotalDividends(BigDecimal totalDividends) { this.totalDividends = totalDividends; }

    public Integer getTransactionCount() { return transactionCount; }
    public void setTransactionCount(Integer transactionCount) { this.transactionCount = transactionCount; }

    public BigDecimal getTotalFees() { return totalFees; }
    public void setTotalFees(BigDecimal totalFees) { this.totalFees = totalFees; }

    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Boolean getIsAtTargetPrice() { return isAtTargetPrice; }
    public void setIsAtTargetPrice(Boolean isAtTargetPrice) { this.isAtTargetPrice = isAtTargetPrice; }

    public Boolean getIsBelowStopLoss() { return isBelowStopLoss; }
    public void setIsBelowStopLoss(Boolean isBelowStopLoss) { this.isBelowStopLoss = isBelowStopLoss; }

    public BigDecimal getTargetPriceDistance() { return targetPriceDistance; }
    public void setTargetPriceDistance(BigDecimal targetPriceDistance) { this.targetPriceDistance = targetPriceDistance; }

    public BigDecimal getStopLossDistance() { return stopLossDistance; }
    public void setStopLossDistance(BigDecimal stopLossDistance) { this.stopLossDistance = stopLossDistance; }

    public Instant getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(Instant purchaseDate) { this.purchaseDate = purchaseDate; }

    public Instant getLastDividendDate() { return lastDividendDate; }
    public void setLastDividendDate(Instant lastDividendDate) { this.lastDividendDate = lastDividendDate; }

    public Instant getLastTransactionDate() { return lastTransactionDate; }
    public void setLastTransactionDate(Instant lastTransactionDate) { this.lastTransactionDate = lastTransactionDate; }

    public Instant getPriceLastUpdated() { return priceLastUpdated; }
    public void setPriceLastUpdated(Instant priceLastUpdated) { this.priceLastUpdated = priceLastUpdated; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "HoldingResponse{" +
                "id='" + id + '\'' +
                ", symbol='" + symbol + '\'' +
                ", companyName='" + companyName + '\'' +
                ", quantity=" + quantity +
                ", averagePrice=" + averagePrice +
                ", currentPrice=" + currentPrice +
                ", totalValue=" + totalValue +
                ", gainLoss=" + gainLoss +
                ", gainLossPercentage=" + gainLossPercentage +
                ", portfolioPercentage=" + portfolioPercentage +
                ", sector='" + sector + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}