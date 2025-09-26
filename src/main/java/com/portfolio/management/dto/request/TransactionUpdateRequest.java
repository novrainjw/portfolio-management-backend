package com.portfolio.management.dto.request;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.time.Instant;

public class TransactionUpdateRequest {
    @DecimalMin(value = "0.01", message = "Price must be positive")
    private BigDecimal price;

    @DecimalMin(value = "0", message = "Fees cannot be negative")
    private BigDecimal fees;

    private Instant transactionDate;
    private String notes;

    // Constructors
    public TransactionUpdateRequest() {
    }

    public TransactionUpdateRequest(BigDecimal price, BigDecimal fees, String notes) {
        this.price = price;
        this.fees = fees;
        this.notes = notes;
    }

    // Getters and setters
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getFees() {
        return fees;
    }

    public void setFees(BigDecimal fees) {
        this.fees = fees;
    }

    public Instant getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Instant transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
