package com.portfolio.management.repository.custom;

import com.portfolio.management.entity.Portfolio;
import com.portfolio.management.enums.Currency;
import com.portfolio.management.enums.Sector;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface PortfolioRepositoryCustom {
    List<Portfolio> findPortfoliosWithDiversification(int minHoldings, int minSectors);

    Map<Sector, BigDecimal> getPortfolioSectorAllocation(String portfolioId);

    List<Portfolio> findSimilarPortfolios(String portfolioId, double similarityThreshold);

    List<Portfolio> findPortfoliosByComplexCriteria(String userId, BigDecimal minValue,
                                                    BigDecimal maxValue, Currency currency,
                                                    String broker, Boolean isActive);
}
