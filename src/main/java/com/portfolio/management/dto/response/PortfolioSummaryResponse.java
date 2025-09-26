package com.portfolio.management.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PortfolioSummaryResponse {
    private String portfolioId;
    private String portfolioName;
    private String currency;

    // Performance Metrics
    private BigDecimal totalValue;
    private BigDecimal totalInvested;
    private BigDecimal totalGainLoss;
    private BigDecimal gainLossPercentage;
    private BigDecimal dayChange;
    private BigDecimal dayChangePercentage;
    private BigDecimal totalDividends;
    private BigDecimal dividendYield;

    // Statistics
    private Integer holdingsCount;
    private Integer activeHoldingsCount;
    private Integer transactionsCount;
    private String topHoldingSymbol;
    private BigDecimal topHoldingPercentage;

    // Allocation Data
    private Map<String, BigDecimal> sectorAllocation;
    private Map<String, BigDecimal> geographicAllocation;
    private Map<String, BigDecimal> assetTypeAllocation;

    // Risk Metrics
    private BigDecimal beta;
    private BigDecimal sharpeRatio;
    private Boolean isDiversified;
    private Integer riskScore;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant lastUpdated;

    // Constructors
    public PortfolioSummaryResponse() {
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private PortfolioSummaryResponse response = new PortfolioSummaryResponse();

        public Builder portfolioId(String portfolioId) {
            response.portfolioId = portfolioId;
            return this;
        }

        public Builder portfolioName(String portfolioName) {
            response.portfolioName = portfolioName;
            return this;
        }

        public Builder currency(String currency) {
            response.currency = currency;
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

        public Builder dividendYield(BigDecimal dividendYield) {
            response.dividendYield = dividendYield;
            return this;
        }

        public Builder holdingsCount(Integer holdingsCount) {
            response.holdingsCount = holdingsCount;
            return this;
        }

        public Builder activeHoldingsCount(Integer activeHoldingsCount) {
            response.activeHoldingsCount = activeHoldingsCount;
            return this;
        }

        public Builder transactionsCount(Integer transactionsCount) {
            response.transactionsCount = transactionsCount;
            return this;
        }

        public Builder topHoldingSymbol(String topHoldingSymbol) {
            response.topHoldingSymbol = topHoldingSymbol;
            return this;
        }

        public Builder topHoldingPercentage(BigDecimal topHoldingPercentage) {
            response.topHoldingPercentage = topHoldingPercentage;
            return this;
        }

        public Builder sectorAllocation(Map<String, BigDecimal> sectorAllocation) {
            response.sectorAllocation = sectorAllocation;
            return this;
        }

        public Builder geographicAllocation(Map<String, BigDecimal> geographicAllocation) {
            response.geographicAllocation = geographicAllocation;
            return this;
        }

        public Builder assetTypeAllocation(Map<String, BigDecimal> assetTypeAllocation) {
            response.assetTypeAllocation = assetTypeAllocation;
            return this;
        }

        public Builder beta(BigDecimal beta) {
            response.beta = beta;
            return this;
        }

        public Builder sharpeRatio(BigDecimal sharpeRatio) {
            response.sharpeRatio = sharpeRatio;
            return this;
        }

        public Builder isDiversified(Boolean isDiversified) {
            response.isDiversified = isDiversified;
            return this;
        }

        public Builder riskScore(Integer riskScore) {
            response.riskScore = riskScore;
            return this;
        }

        public Builder lastUpdated(Instant lastUpdated) {
            response.lastUpdated = lastUpdated;
            return this;
        }

        public PortfolioSummaryResponse build() {
            return response;
        }
    }

    // Getters and setters
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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

    public BigDecimal getDividendYield() {
        return dividendYield;
    }

    public void setDividendYield(BigDecimal dividendYield) {
        this.dividendYield = dividendYield;
    }

    public Integer getHoldingsCount() {
        return holdingsCount;
    }

    public void setHoldingsCount(Integer holdingsCount) {
        this.holdingsCount = holdingsCount;
    }

    public Integer getActiveHoldingsCount() {
        return activeHoldingsCount;
    }

    public void setActiveHoldingsCount(Integer activeHoldingsCount) {
        this.activeHoldingsCount = activeHoldingsCount;
    }

    public Integer getTransactionsCount() {
        return transactionsCount;
    }

    public void setTransactionsCount(Integer transactionsCount) {
        this.transactionsCount = transactionsCount;
    }

    public String getTopHoldingSymbol() {
        return topHoldingSymbol;
    }

    public void setTopHoldingSymbol(String topHoldingSymbol) {
        this.topHoldingSymbol = topHoldingSymbol;
    }

    public BigDecimal getTopHoldingPercentage() {
        return topHoldingPercentage;
    }

    public void setTopHoldingPercentage(BigDecimal topHoldingPercentage) {
        this.topHoldingPercentage = topHoldingPercentage;
    }

    public Map<String, BigDecimal> getSectorAllocation() {
        return sectorAllocation;
    }

    public void setSectorAllocation(Map<String, BigDecimal> sectorAllocation) {
        this.sectorAllocation = sectorAllocation;
    }

    public Map<String, BigDecimal> getGeographicAllocation() {
        return geographicAllocation;
    }

    public void setGeographicAllocation(Map<String, BigDecimal> geographicAllocation) {
        this.geographicAllocation = geographicAllocation;
    }

    public Map<String, BigDecimal> getAssetTypeAllocation() {
        return assetTypeAllocation;
    }

    public void setAssetTypeAllocation(Map<String, BigDecimal> assetTypeAllocation) {
        this.assetTypeAllocation = assetTypeAllocation;
    }

    public BigDecimal getBeta() {
        return beta;
    }

    public void setBeta(BigDecimal beta) {
        this.beta = beta;
    }

    public BigDecimal getSharpeRatio() {
        return sharpeRatio;
    }

    public void setSharpeRatio(BigDecimal sharpeRatio) {
        this.sharpeRatio = sharpeRatio;
    }

    public Boolean getIsDiversified() {
        return isDiversified;
    }

    public void setIsDiversified(Boolean isDiversified) {
        this.isDiversified = isDiversified;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
