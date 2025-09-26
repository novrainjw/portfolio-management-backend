package com.portfolio.management.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.portfolio.management.enums.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionSummaryResponse {
    private String portfolioId;
    private String portfolioName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant endDate;

    // Transaction Counts
    private Integer totalTransactions;
    private Integer buyTransactions;
    private Integer sellTransactions;
    private Integer dividendTransactions;
    private Integer pendingTransactions;
    private Integer failedTransactions;

    // Transaction Amounts
    private BigDecimal totalBuyAmount;
    private BigDecimal totalSellAmount;
    private BigDecimal totalDividendAmount;
    private BigDecimal totalFees;
    private BigDecimal netCashFlow;

    // Performance Metrics
    private BigDecimal realizedGainLoss;
    private BigDecimal totalTradingVolume;
    private BigDecimal averageTransactionSize;
    private BigDecimal averageFeePerTransaction;

    // Breakdown by Type
    private Map<TransactionType, Integer> transactionCountByType;
    private Map<TransactionType, BigDecimal> transactionAmountByType;

    // Daily Volume
    private Map<LocalDate, BigDecimal> dailyVolume;

    // Top Trading Symbols
    private Map<String, Integer> topTradedSymbols;
    private Map<String, BigDecimal> volumeBySymbol;

    // Tax Information
    private BigDecimal taxableDividends;
    private BigDecimal taxableCapitalGains;
    private Integer taxYear;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant lastUpdated;

    // Constructors
    public TransactionSummaryResponse() {
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TransactionSummaryResponse response = new TransactionSummaryResponse();

        public Builder portfolioId(String portfolioId) {
            response.portfolioId = portfolioId;
            return this;
        }

        public Builder portfolioName(String portfolioName) {
            response.portfolioName = portfolioName;
            return this;
        }

        public Builder startDate(Instant startDate) {
            response.startDate = startDate;
            return this;
        }

        public Builder endDate(Instant endDate) {
            response.endDate = endDate;
            return this;
        }

        public Builder totalTransactions(Integer totalTransactions) {
            response.totalTransactions = totalTransactions;
            return this;
        }

        public Builder buyTransactions(Integer buyTransactions) {
            response.buyTransactions = buyTransactions;
            return this;
        }

        public Builder sellTransactions(Integer sellTransactions) {
            response.sellTransactions = sellTransactions;
            return this;
        }

        public Builder dividendTransactions(Integer dividendTransactions) {
            response.dividendTransactions = dividendTransactions;
            return this;
        }

        public Builder pendingTransactions(Integer pendingTransactions) {
            response.pendingTransactions = pendingTransactions;
            return this;
        }

        public Builder failedTransactions(Integer failedTransactions) {
            response.failedTransactions = failedTransactions;
            return this;
        }

        public Builder totalBuyAmount(BigDecimal totalBuyAmount) {
            response.totalBuyAmount = totalBuyAmount;
            return this;
        }

        public Builder totalSellAmount(BigDecimal totalSellAmount) {
            response.totalSellAmount = totalSellAmount;
            return this;
        }

        public Builder totalDividendAmount(BigDecimal totalDividendAmount) {
            response.totalDividendAmount = totalDividendAmount;
            return this;
        }

        public Builder totalFees(BigDecimal totalFees) {
            response.totalFees = totalFees;
            return this;
        }

        public Builder netCashFlow(BigDecimal netCashFlow) {
            response.netCashFlow = netCashFlow;
            return this;
        }

        public Builder realizedGainLoss(BigDecimal realizedGainLoss) {
            response.realizedGainLoss = realizedGainLoss;
            return this;
        }

        public Builder totalTradingVolume(BigDecimal totalTradingVolume) {
            response.totalTradingVolume = totalTradingVolume;
            return this;
        }

        public Builder averageTransactionSize(BigDecimal averageTransactionSize) {
            response.averageTransactionSize = averageTransactionSize;
            return this;
        }

        public Builder averageFeePerTransaction(BigDecimal averageFeePerTransaction) {
            response.averageFeePerTransaction = averageFeePerTransaction;
            return this;
        }

        public Builder transactionCountByType(Map<TransactionType, Integer> transactionCountByType) {
            response.transactionCountByType = transactionCountByType;
            return this;
        }

        public Builder transactionAmountByType(Map<TransactionType, BigDecimal> transactionAmountByType) {
            response.transactionAmountByType = transactionAmountByType;
            return this;
        }

        public Builder dailyVolume(Map<LocalDate, BigDecimal> dailyVolume) {
            response.dailyVolume = dailyVolume;
            return this;
        }

        public Builder topTradedSymbols(Map<String, Integer> topTradedSymbols) {
            response.topTradedSymbols = topTradedSymbols;
            return this;
        }

        public Builder volumeBySymbol(Map<String, BigDecimal> volumeBySymbol) {
            response.volumeBySymbol = volumeBySymbol;
            return this;
        }

        public Builder taxableDividends(BigDecimal taxableDividends) {
            response.taxableDividends = taxableDividends;
            return this;
        }

        public Builder taxableCapitalGains(BigDecimal taxableCapitalGains) {
            response.taxableCapitalGains = taxableCapitalGains;
            return this;
        }

        public Builder taxYear(Integer taxYear) {
            response.taxYear = taxYear;
            return this;
        }

        public Builder lastUpdated(Instant lastUpdated) {
            response.lastUpdated = lastUpdated;
            return this;
        }

        public TransactionSummaryResponse build() {
            return response;
        }
    }

    // Getters and setters (I'll include just a few key ones for brevity)
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

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public Integer getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(Integer totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public BigDecimal getTotalBuyAmount() {
        return totalBuyAmount;
    }

    public void setTotalBuyAmount(BigDecimal totalBuyAmount) {
        this.totalBuyAmount = totalBuyAmount;
    }

    public BigDecimal getTotalSellAmount() {
        return totalSellAmount;
    }

    public void setTotalSellAmount(BigDecimal totalSellAmount) {
        this.totalSellAmount = totalSellAmount;
    }

    public BigDecimal getRealizedGainLoss() {
        return realizedGainLoss;
    }

    public void setRealizedGainLoss(BigDecimal realizedGainLoss) {
        this.realizedGainLoss = realizedGainLoss;
    }

}
