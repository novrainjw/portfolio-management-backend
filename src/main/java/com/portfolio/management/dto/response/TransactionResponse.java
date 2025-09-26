package com.portfolio.management.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.portfolio.management.enums.TransactionStatus;
import com.portfolio.management.enums.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponse {
    private String id;
    private String portfolioId;
    private String portfolioName;
    private String holdingId;
    private TransactionType type;
    private TransactionStatus status;
    private String symbol;
    private String companyName;

    // Transaction Details
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal totalAmount;
    private BigDecimal fees;
    private BigDecimal netAmount;

    // Broker Information
    private String brokerId;
    private String brokerName;
    private String brokerTransactionId;

    // Dates
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant transactionDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant settlementDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant exDividendDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant paymentDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant updatedAt;

    // Performance Impact (calculated fields)
    private BigDecimal impactOnPortfolio;
    private BigDecimal impactPercentage;
    private Boolean isProfitable;
    private BigDecimal gainLossFromTransaction;

    // Tax Information
    private Boolean isTaxable;
    private Integer taxYear;
    private BigDecimal taxableAmount;

    private String notes;
    private String failureReason;

    // Constructors
    public TransactionResponse() {}

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TransactionResponse response = new TransactionResponse();

        public Builder id(String id) { response.id = id; return this; }
        public Builder portfolioId(String portfolioId) { response.portfolioId = portfolioId; return this; }
        public Builder portfolioName(String portfolioName) { response.portfolioName = portfolioName; return this; }
        public Builder holdingId(String holdingId) { response.holdingId = holdingId; return this; }
        public Builder type(TransactionType type) { response.type = type; return this; }
        public Builder status(TransactionStatus status) { response.status = status; return this; }
        public Builder symbol(String symbol) { response.symbol = symbol; return this; }
        public Builder companyName(String companyName) { response.companyName = companyName; return this; }
        public Builder quantity(BigDecimal quantity) { response.quantity = quantity; return this; }
        public Builder price(BigDecimal price) { response.price = price; return this; }
        public Builder totalAmount(BigDecimal totalAmount) { response.totalAmount = totalAmount; return this; }
        public Builder fees(BigDecimal fees) { response.fees = fees; return this; }
        public Builder netAmount(BigDecimal netAmount) { response.netAmount = netAmount; return this; }
        public Builder brokerId(String brokerId) { response.brokerId = brokerId; return this; }
        public Builder brokerName(String brokerName) { response.brokerName = brokerName; return this; }
        public Builder brokerTransactionId(String brokerTransactionId) { response.brokerTransactionId = brokerTransactionId; return this; }
        public Builder transactionDate(Instant transactionDate) { response.transactionDate = transactionDate; return this; }
        public Builder settlementDate(Instant settlementDate) { response.settlementDate = settlementDate; return this; }
        public Builder exDividendDate(Instant exDividendDate) { response.exDividendDate = exDividendDate; return this; }
        public Builder paymentDate(Instant paymentDate) { response.paymentDate = paymentDate; return this; }
        public Builder createdAt(Instant createdAt) { response.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { response.updatedAt = updatedAt; return this; }
        public Builder impactOnPortfolio(BigDecimal impactOnPortfolio) { response.impactOnPortfolio = impactOnPortfolio; return this; }
        public Builder impactPercentage(BigDecimal impactPercentage) { response.impactPercentage = impactPercentage; return this; }
        public Builder isProfitable(Boolean isProfitable) { response.isProfitable = isProfitable; return this; }
        public Builder gainLossFromTransaction(BigDecimal gainLossFromTransaction) { response.gainLossFromTransaction = gainLossFromTransaction; return this; }
        public Builder isTaxable(Boolean isTaxable) { response.isTaxable = isTaxable; return this; }
        public Builder taxYear(Integer taxYear) { response.taxYear = taxYear; return this; }
        public Builder taxableAmount(BigDecimal taxableAmount) { response.taxableAmount = taxableAmount; return this; }
        public Builder notes(String notes) { response.notes = notes; return this; }
        public Builder failureReason(String failureReason) { response.failureReason = failureReason; return this; }

        public TransactionResponse build() { return response; }
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPortfolioId() { return portfolioId; }
    public void setPortfolioId(String portfolioId) { this.portfolioId = portfolioId; }

    public String getPortfolioName() { return portfolioName; }
    public void setPortfolioName(String portfolioName) { this.portfolioName = portfolioName; }

    public String getHoldingId() { return holdingId; }
    public void setHoldingId(String holdingId) { this.holdingId = holdingId; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getFees() { return fees; }
    public void setFees(BigDecimal fees) { this.fees = fees; }

    public BigDecimal getNetAmount() { return netAmount; }
    public void setNetAmount(BigDecimal netAmount) { this.netAmount = netAmount; }

    public String getBrokerId() { return brokerId; }
    public void setBrokerId(String brokerId) { this.brokerId = brokerId; }

    public String getBrokerName() { return brokerName; }
    public void setBrokerName(String brokerName) { this.brokerName = brokerName; }

    public String getBrokerTransactionId() { return brokerTransactionId; }
    public void setBrokerTransactionId(String brokerTransactionId) { this.brokerTransactionId = brokerTransactionId; }

    public Instant getTransactionDate() { return transactionDate; }
    public void setTransactionDate(Instant transactionDate) { this.transactionDate = transactionDate; }

    public Instant getSettlementDate() { return settlementDate; }
    public void setSettlementDate(Instant settlementDate) { this.settlementDate = settlementDate; }

    public Instant getExDividendDate() { return exDividendDate; }
    public void setExDividendDate(Instant exDividendDate) { this.exDividendDate = exDividendDate; }

    public Instant getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Instant paymentDate) { this.paymentDate = paymentDate; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public BigDecimal getImpactOnPortfolio() { return impactOnPortfolio; }
    public void setImpactOnPortfolio(BigDecimal impactOnPortfolio) { this.impactOnPortfolio = impactOnPortfolio; }

    public BigDecimal getImpactPercentage() { return impactPercentage; }
    public void setImpactPercentage(BigDecimal impactPercentage) { this.impactPercentage = impactPercentage; }

    public Boolean getIsProfitable() { return isProfitable; }
    public void setIsProfitable(Boolean isProfitable) { this.isProfitable = isProfitable; }

    public BigDecimal getGainLossFromTransaction() { return gainLossFromTransaction; }
    public void setGainLossFromTransaction(BigDecimal gainLossFromTransaction) { this.gainLossFromTransaction = gainLossFromTransaction; }

    public Boolean getIsTaxable() { return isTaxable; }
    public void setIsTaxable(Boolean isTaxable) { this.isTaxable = isTaxable; }

    public Integer getTaxYear() { return taxYear; }
    public void setTaxYear(Integer taxYear) { this.taxYear = taxYear; }

    public BigDecimal getTaxableAmount() { return taxableAmount; }
    public void setTaxableAmount(BigDecimal taxableAmount) { this.taxableAmount = taxableAmount; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
}
