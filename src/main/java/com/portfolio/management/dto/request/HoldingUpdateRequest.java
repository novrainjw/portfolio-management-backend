package com.portfolio.management.dto.request;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public class HoldingUpdateRequest {
    @DecimalMin(value = "0.01", message = "Target price must be positive")
    private BigDecimal targetPrice;

    @DecimalMin(value = "0.01", message = "Stop loss price must be positive")
    private BigDecimal stopLossPrice;

    private String notes;

    // Constructors
    public HoldingUpdateRequest() {}

    public HoldingUpdateRequest(BigDecimal targetPrice, BigDecimal stopLossPrice, String notes) {
        this.targetPrice = targetPrice;
        this.stopLossPrice = stopLossPrice;
        this.notes = notes;
    }

    // Getters and setters
    public BigDecimal getTargetPrice() { return targetPrice; }
    public void setTargetPrice(BigDecimal targetPrice) { this.targetPrice = targetPrice; }

    public BigDecimal getStopLossPrice() { return stopLossPrice; }
    public void setStopLossPrice(BigDecimal stopLossPrice) { this.stopLossPrice = stopLossPrice; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
