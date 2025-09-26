package com.portfolio.management.mapper;

import com.portfolio.management.dto.request.HoldingCreateRequest;
import com.portfolio.management.dto.request.HoldingUpdateRequest;
import com.portfolio.management.dto.response.HoldingResponse;
import com.portfolio.management.dto.response.HoldingSummaryResponse;
import com.portfolio.management.entity.Holding;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * MapStruct mapper for Holding entity and DTOs
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface HoldingMapper {
    /**
     * Convert Holding entity to HoldingResponse DTO
     */
    @Mapping(target = "portfolioName", source = "portfolio.name")
    @Mapping(target = "totalValue", ignore = true) // Will be calculated
    @Mapping(target = "totalInvested", ignore = true) // Will be calculated
    @Mapping(target = "gainLoss", ignore = true) // Will be calculated
    @Mapping(target = "gainLossPercentage", ignore = true) // Will be calculated
    @Mapping(target = "dayChange", ignore = true) // Will be calculated
    @Mapping(target = "dayChangePercentage", ignore = true) // Will be calculated
    @Mapping(target = "totalDividends", ignore = true) // Will be calculated
    @Mapping(target = "transactionCount", ignore = true) // Will be calculated
    @Mapping(target = "totalFees", ignore = true) // Will be calculated
    @Mapping(target = "portfolioPercentage", ignore = true) // Will be calculated
    @Mapping(target = "isAtTargetPrice", ignore = true) // Will be calculated
    @Mapping(target = "isBelowStopLoss", ignore = true) // Will be calculated
    @Mapping(target = "targetPriceDistance", ignore = true) // Will be calculated
    @Mapping(target = "stopLossDistance", ignore = true) // Will be calculated
    @Mapping(target = "lastTransactionDate", ignore = true) // Will be calculated
    @Mapping(target = "priceLastUpdated", source = "updatedAt")
    HoldingResponse toResponse(Holding holding);

    /**
     * Convert HoldingCreateRequest to Holding entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "portfolio", ignore = true) // Will be set separately
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "updatedAt", ignore = true)
    Holding toEntity(HoldingCreateRequest request);

    /**
     * Update Holding entity from HoldingUpdateRequest
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "portfolio", ignore = true)
    @Mapping(target = "symbol", ignore = true)
    @Mapping(target = "quantity", ignore = true)
    @Mapping(target = "averagePrice", ignore = true)
    @Mapping(target = "currentPrice", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    void updateEntityFromRequest(HoldingUpdateRequest request, @MappingTarget Holding holding);

    /**
     * Convert list of Holding entities to list of HoldingResponse DTOs
     */
    List<HoldingResponse> toResponseList(List<Holding> holdings);

    /**
     * Create HoldingResponse with calculated financial data
     */
    @Mapping(target = "portfolioName", source = "holding.portfolio.name")
    @Mapping(target = "totalValue", source = "totalValue")
    @Mapping(target = "totalInvested", source = "totalInvested")
    @Mapping(target = "gainLoss", source = "gainLoss")
    @Mapping(target = "gainLossPercentage", source = "gainLossPercentage")
    @Mapping(target = "dayChange", source = "dayChange")
    @Mapping(target = "dayChangePercentage", source = "dayChangePercentage")
    @Mapping(target = "totalDividends", source = "totalDividends")
    @Mapping(target = "transactionCount", source = "transactionCount")
    @Mapping(target = "totalFees", source = "totalFees")
    @Mapping(target = "portfolioPercentage", source = "portfolioPercentage")
    @Mapping(target = "priceLastUpdated", source = "holding.updatedAt")
    HoldingResponse toResponseWithFinancials(Holding holding,
                                             BigDecimal totalValue,
                                             BigDecimal totalInvested,
                                             BigDecimal gainLoss,
                                             BigDecimal gainLossPercentage,
                                             BigDecimal dayChange,
                                             BigDecimal dayChangePercentage,
                                             BigDecimal totalDividends,
                                             Integer transactionCount,
                                             BigDecimal totalFees,
                                             BigDecimal portfolioPercentage);

    /**
     * Convert Holding to HoldingSummaryResponse
     */
    @Mapping(target = "holdingId", source = "holding.id")
    @Mapping(target = "portfolioName", source = "holding.portfolio.name")
    @Mapping(target = "totalValue", source = "totalValue")
    @Mapping(target = "totalInvested", source = "totalInvested")
    @Mapping(target = "gainLoss", source = "gainLoss")
    @Mapping(target = "gainLossPercentage", source = "gainLossPercentage")
    @Mapping(target = "dayChange", source = "dayChange")
    @Mapping(target = "dayChangePercentage", source = "dayChangePercentage")
    @Mapping(target = "portfolioPercentage", source = "portfolioPercentage")
    @Mapping(target = "transactionCount", source = "transactionCount")
    @Mapping(target = "totalFees", source = "totalFees")
    @Mapping(target = "totalDividends", source = "totalDividends")
    @Mapping(target = "lastUpdated", expression = "java(java.time.Instant.now())")
    HoldingSummaryResponse toSummaryResponse(Holding holding,
                                             BigDecimal totalValue,
                                             BigDecimal totalInvested,
                                             BigDecimal gainLoss,
                                             BigDecimal gainLossPercentage,
                                             BigDecimal dayChange,
                                             BigDecimal dayChangePercentage,
                                             BigDecimal portfolioPercentage,
                                             Integer transactionCount,
                                             BigDecimal totalFees,
                                             BigDecimal totalDividends);

    // Helper methods

    /**
     * Calculate total value from quantity and current price
     */
    default BigDecimal calculateTotalValue(BigDecimal quantity, BigDecimal currentPrice) {
        if (quantity == null || currentPrice == null) {
            return BigDecimal.ZERO;
        }
        return quantity.multiply(currentPrice);
    }

    /**
     * Calculate total invested from quantity and average price
     */
    default BigDecimal calculateTotalInvested(BigDecimal quantity, BigDecimal averagePrice) {
        if (quantity == null || averagePrice == null) {
            return BigDecimal.ZERO;
        }
        return quantity.multiply(averagePrice);
    }

    /**
     * After mapping method to perform calculations
     */
    @AfterMapping
    default void afterMapping(@MappingTarget HoldingResponse holdingResponse, Holding holding) {
        if (holdingResponse != null && holding != null) {
            // Calculate values if not already set
            if (holdingResponse.getTotalValue() == null) {
                holdingResponse.setTotalValue(
                        calculateTotalValue(holding.getQuantity(), holding.getCurrentPrice())
                );
            }
            if (holdingResponse.getTotalInvested() == null) {
                holdingResponse.setTotalInvested(
                        calculateTotalInvested(holding.getQuantity(), holding.getAveragePrice())
                );
            }

            // Calculate gain/loss if both values are available
            if (holdingResponse.getTotalValue() != null && holdingResponse.getTotalInvested() != null) {
                BigDecimal gainLoss = holdingResponse.getTotalValue().subtract(holdingResponse.getTotalInvested());
                holdingResponse.setGainLoss(gainLoss);

                // Calculate percentage if total invested is not zero
                if (holdingResponse.getTotalInvested().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal percentage = gainLoss.divide(holdingResponse.getTotalInvested(), 4, java.math.RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));
                    holdingResponse.setGainLossPercentage(percentage);
                }
            }
        }
    }
}
