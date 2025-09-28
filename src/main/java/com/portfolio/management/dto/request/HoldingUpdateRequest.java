package com.portfolio.management.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Request DTO for updating an existing holding
 */
public class HoldingUpdateRequest {

    @Size(max = 255, message = "Company name cannot exceed 255 characters")
    private String companyName;

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
    public HoldingUpdateRequest() {}

    // Getters and Setters
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public BigDecimal getTargetPrice() { return targetPrice; }
    public void setTargetPrice(BigDecimal targetPrice) { this.targetPrice = targetPrice; }

    public BigDecimal getStopLossPrice() { return stopLossPrice; }
    public void setStopLossPrice(BigDecimal stopLossPrice) { this.stopLossPrice = stopLossPrice; }

    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Instant getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(Instant purchaseDate) { this.purchaseDate = purchaseDate; }

    @Override
    public String toString() {
        return "HoldingUpdateRequest{" +
                "companyName='" + companyName + '\'' +
                ", targetPrice=" + targetPrice +
                ", stopLossPrice=" + stopLossPrice +
                ", sector='" + sector + '\'' +
                ", country='" + country + '\'' +
                ", notes='" + notes + '\'' +
                ", purchaseDate=" + purchaseDate +
                '}';
    }
}