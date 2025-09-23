package com.portfolio.management.repository.custom;

import com.portfolio.management.entity.Holding;
import com.portfolio.management.enums.HoldingType;
import com.portfolio.management.enums.Sector;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface HoldingRepositoryCustom {
    List<Holding> findTopHoldingsByPerformance(String userId, int limit);

    Map<Sector, List<Holding>> getHoldingsGroupedBySector(String portfolioId);

    List<Holding> findHoldingsByRiskLevel(String userId, String riskLevel);

    BigDecimal calculatePortfolioDiversificationScore(String portfolioId);

    List<Object[]> getPerformanceComparison(String symbol, String userId);
}
