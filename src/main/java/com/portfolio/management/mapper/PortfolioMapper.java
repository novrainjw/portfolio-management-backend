package com.portfolio.management.mapper;

import com.portfolio.management.dto.request.PortfolioCreateRequest;
import com.portfolio.management.dto.request.PortfolioUpdateRequest;
import com.portfolio.management.dto.response.PortfolioResponse;
import com.portfolio.management.dto.response.PortfolioSummaryResponse;
import com.portfolio.management.entity.Portfolio;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * MapStruct mapper for Portfolio entity and DTOs
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface PortfolioMapper {
    /**
     * Convert Portfolio entity to PortfolioResponse DTO
     */
    @Mapping(target = "userName", source = "user.username")
    @Mapping(target = "totalValue", ignore = true) // Will be calculated
    @Mapping(target = "totalInvested", ignore = true) // Will be calculated
    @Mapping(target = "totalGainLoss", ignore = true) // Will be calculated
    @Mapping(target = "gainLossPercentage", ignore = true) // Will be calculated
    @Mapping(target = "dayChange", ignore = true) // Will be calculated
    @Mapping(target = "dayChangePercentage", ignore = true) // Will be calculated
    @Mapping(target = "totalDividends", ignore = true) // Will be calculated
    @Mapping(target = "holdingsCount", ignore = true) // Will be calculated
    @Mapping(target = "transactionsCount", ignore = true) // Will be calculated
    @Mapping(target = "topHoldings", ignore = true)
    @Mapping(target = "recentTransactions", ignore = true)
    PortfolioResponse toResponse(Portfolio portfolio);

    /**
     * Convert PortfolioCreateRequest to Portfolio entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true) // Will be set separately
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "holdings", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "updatedAt", ignore = true)
    Portfolio toEntity(PortfolioCreateRequest request);

    /**
     * Update Portfolio entity from PortfolioUpdateRequest
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "currency", ignore = true) // Cannot be changed
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "holdings", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    void updateEntityFromRequest(PortfolioUpdateRequest request, @MappingTarget Portfolio portfolio);

    /**
     * Convert list of Portfolio entities to list of PortfolioResponse DTOs
     */
    List<PortfolioResponse> toResponseList(List<Portfolio> portfolios);

    /**
     * Create PortfolioResponse with calculated financial data
     */
    @Mapping(target = "userName", source = "portfolio.user.username")
    @Mapping(target = "totalValue", source = "totalValue")
    @Mapping(target = "totalInvested", source = "totalInvested")
    @Mapping(target = "totalGainLoss", source = "totalGainLoss")
    @Mapping(target = "gainLossPercentage", source = "gainLossPercentage")
    @Mapping(target = "dayChange", source = "dayChange")
    @Mapping(target = "dayChangePercentage", source = "dayChangePercentage")
    @Mapping(target = "totalDividends", source = "totalDividends")
    @Mapping(target = "holdingsCount", source = "holdingsCount")
    @Mapping(target = "transactionsCount", source = "transactionsCount")
    @Mapping(target = "topHoldings", ignore = true)
    @Mapping(target = "recentTransactions", ignore = true)
    PortfolioResponse toResponseWithFinancials(Portfolio portfolio,
                                               BigDecimal totalValue,
                                               BigDecimal totalInvested,
                                               BigDecimal totalGainLoss,
                                               BigDecimal gainLossPercentage,
                                               BigDecimal dayChange,
                                               BigDecimal dayChangePercentage,
                                               BigDecimal totalDividends,
                                               Integer holdingsCount,
                                               Integer transactionsCount);

    /**
     * Convert Portfolio to PortfolioSummaryResponse
     */
    @Mapping(target = "portfolioId", source = "portfolio.id")
    @Mapping(target = "portfolioName", source = "portfolio.name")
    @Mapping(target = "currency", source = "portfolio.currency")
    @Mapping(target = "totalValue", source = "totalValue")
    @Mapping(target = "totalInvested", source = "totalInvested")
    @Mapping(target = "totalGainLoss", source = "totalGainLoss")
    @Mapping(target = "gainLossPercentage", source = "gainLossPercentage")
    @Mapping(target = "dayChange", source = "dayChange")
    @Mapping(target = "dayChangePercentage", source = "dayChangePercentage")
    @Mapping(target = "totalDividends", source = "totalDividends")
    @Mapping(target = "holdingsCount", source = "holdingsCount")
    @Mapping(target = "transactionsCount", source = "transactionsCount")
    @Mapping(target = "lastUpdated", expression = "java(java.time.Instant.now())")
    PortfolioSummaryResponse toSummaryResponse(Portfolio portfolio,
                                               BigDecimal totalValue,
                                               BigDecimal totalInvested,
                                               BigDecimal totalGainLoss,
                                               BigDecimal gainLossPercentage,
                                               BigDecimal dayChange,
                                               BigDecimal dayChangePercentage,
                                               BigDecimal totalDividends,
                                               Integer holdingsCount,
                                               Integer transactionsCount);

    /**
     * Create minimal PortfolioResponse (for lists and performance)
     */
    @Mapping(target = "userName", source = "user.username")
    @Mapping(target = "totalValue", ignore = true)
    @Mapping(target = "totalInvested", ignore = true)
    @Mapping(target = "totalGainLoss", ignore = true)
    @Mapping(target = "gainLossPercentage", ignore = true)
    @Mapping(target = "dayChange", ignore = true)
    @Mapping(target = "dayChangePercentage", ignore = true)
    @Mapping(target = "totalDividends", ignore = true)
    @Mapping(target = "holdingsCount", ignore = true)
    @Mapping(target = "transactionsCount", ignore = true)
    @Mapping(target = "topHoldings", ignore = true)
    @Mapping(target = "recentTransactions", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PortfolioResponse toMinimalResponse(Portfolio portfolio);

    // Helper methods for complex mappings

    /**
     * Map status enum to string if needed
     */
    default String statusToString(com.portfolio.management.enums.PortfolioStatus status) {
        return status != null ? status.name() : null;
    }

    /**
     * Before mapping method to perform any pre-processing
     */
    @BeforeMapping
    default void beforeMapping(@MappingTarget PortfolioResponse.Builder builder, Portfolio portfolio) {
        // Any pre-processing logic can be added here
    }

    /**
     * After mapping method to perform any post-processing
     */
    @AfterMapping
    default void afterMapping(@MappingTarget PortfolioResponse portfolioResponse, Portfolio portfolio) {
        // Any post-processing logic can be added here
        if (portfolioResponse != null && portfolio != null) {
            // Set calculated fields to zero if null
            if (portfolioResponse.getTotalValue() == null) {
                portfolioResponse.setTotalValue(BigDecimal.ZERO);
            }
            if (portfolioResponse.getTotalInvested() == null) {
                portfolioResponse.setTotalInvested(BigDecimal.ZERO);
            }
        }
    }
}
