package com.portfolio.management.entity;

import com.portfolio.management.enums.Currency;
import com.portfolio.management.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transactions",
        indexes = {
                @Index(name = "idx_transactions_portfolio_id", columnList = "portfolio_id"),
                @Index(name = "idx_transactions_holding_id", columnList = "holding_id"),
                @Index(name = "idx_transactions_type", columnList = "type"),
                @Index(name = "idx_transactions_symbol", columnList = "symbol"),
                @Index(name = "idx_transactions_transaction_date", columnList = "transaction_date")
        })
public class Transaction extends BaseEntity {
    @Id
    @UuidGenerator
    @Column(name = "id", length = 50)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "portfolio_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transaction_portfolio"))
    private Portfolio portfolio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "holding_id", foreignKey = @ForeignKey(name = "fk_transaction_holding"))
    private Holding holding;

    @NotNull(message = "Transaction type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private TransactionType type;

    @NotBlank(message = "Symbol is required")
    @Size(min = 1, max = 20, message = "Symbol must be between 1 and 20 characters")
    @Column(name = "symbol", nullable = false, length = 20)
    private String symbol;

    @NotNull(message = "Quantity is required")
    @Digits(integer = 15, fraction = 6, message = "Quantity must have at most 15 integer digits and 6 decimal places")
    @Column(name = "quantity", precision = 15, scale = 6, nullable = false)
    private BigDecimal quantity;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0000", message = "Price must be greater than or equal to 0")
    @Digits(integer = 15, fraction = 4, message = "Price must have at most 15 integer digits and 4 decimal places")
    @Column(name = "price", precision = 15, scale = 4, nullable = false)
    private BigDecimal price;

    @Column(name = "total_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @DecimalMin(value = "0.00", message = "Fees must be greater than or equal to 0")
    @Digits(integer = 15, fraction = 2, message = "Fees must have at most 15 integer digits and 2 decimal places")
    @Column(name = "fees", precision = 15, scale = 2, nullable = false)
    private BigDecimal fees = BigDecimal.ZERO;

    @NotNull(message = "Currency is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, length = 3)
    private Currency currency;

    @NotNull(message = "Transaction date is required")
    @Column(name = "transaction_date", nullable = false)
    private Instant transactionDate;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Constructors
    public Transaction() {
    }

    public Transaction(Portfolio portfolio, Holding holding, TransactionType type, String symbol,
                       BigDecimal quantity, BigDecimal price, BigDecimal fees, Currency currency,
                       Instant transactionDate, String notes) {
        this.portfolio = portfolio;
        this.holding = holding;
        this.type = type;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.fees = fees;
        this.currency = currency;
        this.transactionDate = transactionDate;
        this.notes = notes;
        calculateTotalAmount();
    }

    // Business Methods
    public void calculateTotalAmount() {
        BigDecimal baseAmount = quantity.multiply(price);
        this.totalAmount = baseAmount.add(fees);
    }

    public boolean isBuyTransaction() {
        return TransactionType.BUY.equals(type);
    }

    public boolean isSellTransaction() {
        return TransactionType.SELL.equals(type);
    }

    public boolean isDividendTransaction() {
        return TransactionType.DIVIDEND.equals(type);
    }

    public String getDisplaySymbol() {
        return symbol.toUpperCase();
    }

    public BigDecimal getNetAmount() {
        return isSellTransaction() ? totalAmount.subtract(fees) : totalAmount;
    }

    @PrePersist
    @PreUpdate
    private void updateCalculatedFields() {
        calculateTotalAmount();
    }

    // Getters and Setters
    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public Holding getHolding() {
        return holding;
    }

    public void setHolding(Holding holding) {
        this.holding = holding;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getFees() {
        return fees;
    }

    public void setFees(BigDecimal fees) {
        this.fees = fees;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
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
