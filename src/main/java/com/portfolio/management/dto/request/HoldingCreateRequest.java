package com.portfolio.management.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;

public class HoldingCreateRequest {
    @NotBlank(message = "Symbol is required")
    @Size(max = 10, message = "Symbol cannot exceed 10 characters")
    private String symbol;

    @Size(max = 255, message = "Company name cannot exceed 255 characters")
    private String companyName;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.000001", message = "Quantity must be greater than zero")
    @Digits(integer = 15, fraction = 6, message = "Quantity format is invalid")
    private BigDecimal quantity;

    @DecimalMin(value = "0.01", message = "Average price must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "Average price format is invalid")
    private BigDecimal averagePrice;

    @DecimalMin(value = "0.01", message = "Current price must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "Current price format is invalid")
    private BigDecimal currentPrice;

    @DecimalMin(value = "0.01", message = "Target price must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "Target price format is invalid")
    private BigDecimal targetPrice;

    @DecimalMin(value = "0.01", message = "Stop loss price must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "Stop loss price format is invalid")
    private BigDecimal stopLossPrice;

    @Size(max = 100, message = "Sector cannot exceed 100 characters")
    private String sector;

    @Size(max = 100, message = "Country cannot exceed 100 characters")
    private String country;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant purchaseDate;

    // Constructors
    public HoldingCreateRequest() {
    }

    public HoldingCreateRequest(String symbol, BigDecimal quantity, BigDecimal averagePrice) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.averagePrice = averagePrice;
    }

    // Getters and Setters
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

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Instant purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    @Override
    public String toString() {
        return "HoldingCreateRequest{" +
                "symbol='" + symbol + '\'' +
                ", companyName='" + companyName + '\'' +
                ", quantity=" + quantity +
                ", averagePrice=" + averagePrice +
                ", currentPrice=" + currentPrice +
                ", targetPrice=" + targetPrice +
                ", stopLossPrice=" + stopLossPrice +
                ", sector='" + sector + '\'' +
                ", country='" + country + '\'' +
                ", notes='" + notes + '\'' +
                ", purchaseDate=" + purchaseDate +
                '}';
    }
}
