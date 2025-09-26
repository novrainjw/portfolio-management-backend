package com.portfolio.management.mapper;

import com.portfolio.management.dto.request.TransactionCreateRequest;
import com.portfolio.management.dto.request.TransactionUpdateRequest;
import com.portfolio.management.dto.response.TransactionResponse;
import com.portfolio.management.dto.response.TransactionSummaryResponse;
import com.portfolio.management.entity.Transaction;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * MapStruct mapper for Transaction entity and DTOs
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface TransactionMapper {
    /**
     * Convert Transaction entity to TransactionResponse DTO
     */
    @Mapping(target = "portfolioName", source = "portfolio.name")
    @Mapping(target = "brokerName", source = "broker.name")
    @Mapping(target = "totalAmount", ignore = true) // Will be calculated
    @Mapping(target = "netAmount", ignore = true) // Will be calculated
    @Mapping(target = "impactOnPortfolio", ignore = true) // Will be calculated
    @Mapping(target = "impactPercentage", ignore = true) // Will be calculated
    @Mapping(target = "isProfitable", ignore = true) // Will be calculated
    @Mapping(target = "gainLossFromTransaction", ignore = true) // Will be calculated
    @Mapping(target = "isTaxable", ignore = true) // Will be calculated
    @Mapping(target = "taxYear", ignore = true) // Will be calculated
    @Mapping(target = "taxableAmount", ignore = true)
    // Will be calculated
    TransactionResponse toResponse(Transaction transaction);

    /**
     * Convert TransactionCreateRequest to Transaction entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "portfolio", ignore = true) // Will be set separately
    @Mapping(target = "holding", ignore = true) // Will be set separately
    @Mapping(target = "broker", ignore = true) // Will be set separately
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "brokerTransactionId", ignore = true)
    @Mapping(target = "settlementDate", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "transactionDate", expression = "java(request.getTransactionDate() != null ? request.getTransactionDate() : java.time.Instant.now())")
    Transaction toEntity(TransactionCreateRequest request);

    /**
     * Update Transaction entity from TransactionUpdateRequest
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "portfolio", ignore = true)
    @Mapping(target = "holding", ignore = true)
    @Mapping(target = "broker", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "symbol", ignore = true)
    @Mapping(target = "quantity", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "brokerTransactionId", ignore = true)
    @Mapping(target = "settlementDate", ignore = true)
    @Mapping(target = "exDividendDate", ignore = true)
    @Mapping(target = "paymentDate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    void updateEntityFromRequest(TransactionUpdateRequest request, @MappingTarget Transaction transaction);

    /**
     * Convert list of Transaction entities to list of TransactionResponse DTOs
     */
    List<TransactionResponse> toResponseList(List<Transaction> transactions);

    /**
     * Create TransactionResponse with calculated financial data
     */
    @Mapping(target = "portfolioName", source = "transaction.portfolio.name")
    @Mapping(target = "brokerName", source = "transaction.broker.name")
    @Mapping(target = "totalAmount", source = "totalAmount")
    @Mapping(target = "netAmount", source = "netAmount")
    @Mapping(target = "impactOnPortfolio", source = "impactOnPortfolio")
    @Mapping(target = "impactPercentage", source = "impactPercentage")
    @Mapping(target = "isProfitable", source = "isProfitable")
    @Mapping(target = "gainLossFromTransaction", source = "gainLossFromTransaction")
    TransactionResponse toResponseWithFinancials(Transaction transaction,
                                                 BigDecimal totalAmount,
                                                 BigDecimal netAmount,
                                                 BigDecimal impactOnPortfolio,
                                                 BigDecimal impactPercentage,
                                                 Boolean isProfitable,
                                                 BigDecimal gainLossFromTransaction);

    // Helper methods

    /**
     * Calculate total amount from quantity and price
     */
    default BigDecimal calculateTotalAmount(BigDecimal quantity, BigDecimal price) {
        if (quantity == null || price == null) {
            return BigDecimal.ZERO;
        }
        return quantity.multiply(price);
    }

    /**
     * Calculate net amount considering fees and transaction type
     */
    default BigDecimal calculateNetAmount(BigDecimal totalAmount, BigDecimal fees,
                                          com.portfolio.management.enums.TransactionType type) {
        if (totalAmount == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal feeAmount = fees != null ? fees : BigDecimal.ZERO;

        // For buy transactions, net amount is total + fees (money out)
        // For sell transactions, net amount is total - fees (money in)
        if (type == com.portfolio.management.enums.TransactionType.BUY) {
            return totalAmount.add(feeAmount);
        } else {
            return totalAmount.subtract(feeAmount);
        }
    }

    /**
     * After mapping method to perform calculations
     */
    @AfterMapping
    default void afterMapping(@MappingTarget TransactionResponse transactionResponse, Transaction transaction) {
        if (transactionResponse != null && transaction != null) {
            // Calculate total amount if not already set
            if (transactionResponse.getTotalAmount() == null) {
                BigDecimal totalAmount = calculateTotalAmount(transaction.getQuantity(), transaction.getPrice());
                transactionResponse.setTotalAmount(totalAmount);
            }

            // Calculate net amount if not already set
            if (transactionResponse.getNetAmount() == null) {
                BigDecimal netAmount = calculateNetAmount(
                        transactionResponse.getTotalAmount(),
                        transaction.getFees(),
                        transaction.getType()
                );
                transactionResponse.setNetAmount(netAmount);
            }
        }
    }

    /**
     * Map transaction type enum to string if needed
     */
    default String transactionTypeToString(com.portfolio.management.enums.TransactionType type) {
        return type != null ? type.name() : null;
    }

    /**
     * Map transaction status enum to string if needed
     */
    default String transactionStatusToString(com.portfolio.management.enums.TransactionStatus status) {
        return status != null ? status.name() : null;
    }
}
