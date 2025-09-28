package com.portfolio.management.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Summary response DTO for holding information with essential metrics
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HoldingSummaryResponse {

    private String holdingId;
    private String symbol;
    private String companyName;
    private String portfolioName;
    private String sector;
    private String country;

    // Core financial data
    private BigDecimal quantity;
    private BigDecimal currentPrice;
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

    // Status and alerts
    private String status;
    private Boolean isAtTargetPrice;
    private Boolean isBelowStopLoss;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant lastUpdated;

    // Constructors
    public HoldingSummaryResponse() {}

    // Getters and Setters
    public String getHoldingId() { return holdingId; }
    public void setHoldingId(String holdingId) { this.holdingId = holdingId; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getPortfolioName() { return portfolioName; }
    public void setPortfolioName(String portfolioName) { this.portfolioName = portfolioName; }

    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }

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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Boolean getIsAtTargetPrice() { return isAtTargetPrice; }
    public void setIsAtTargetPrice(Boolean isAtTargetPrice) { this.isAtTargetPrice = isAtTargetPrice; }

    public Boolean getIsBelowStopLoss() { return isBelowStopLoss; }
    public void setIsBelowStopLoss(Boolean isBelowStopLoss) { this.isBelowStopLoss = isBelowStopLoss; }

    public Instant getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Instant lastUpdated) { this.lastUpdated = lastUpdated; }

    @Override
    public String toString() {
        return "HoldingSummaryResponse{" +
                "holdingId='" + holdingId + '\'' +
                ", symbol='" + symbol + '\'' +
                ", companyName='" + companyName + '\'' +
                ", totalValue=" + totalValue +
                ", gainLoss=" + gainLoss +
                ", gainLossPercentage=" + gainLossPercentage +
                ", portfolioPercentage=" + portfolioPercentage +
                ", status='" + status + '\'' +
                '}';
    }
}