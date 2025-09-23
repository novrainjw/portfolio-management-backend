package com.portfolio.management.repository.custom.impl;

import com.portfolio.management.entity.Holding;
import com.portfolio.management.enums.HoldingType;
import com.portfolio.management.enums.Sector;
import com.portfolio.management.repository.custom.HoldingRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;

@Repository
public class HoldingRepositoryImpl implements HoldingRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Holding> findTopHoldingsByPerformance(String userId, int limit) {
        String jpql = """
                SELECT h FROM Holding h 
                WHERE h.portfolio.userId = :userId 
                ORDER BY h.gainLossPercent DESC
                """;

        TypedQuery<Holding> query = entityManager.createQuery(jpql, Holding.class);
        query.setParameter("userId", userId);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public Map<Sector, List<Holding>> getHoldingsGroupedBySector(String portfolioId) {
        String jpql = """
                SELECT h FROM Holding h 
                WHERE h.portfolio.id = :portfolioId 
                ORDER BY h.sector, h.currentValue DESC
                """;

        TypedQuery<Holding> query = entityManager.createQuery(jpql, Holding.class);
        query.setParameter("portfolioId", portfolioId);

        List<Holding> holdings = query.getResultList();
        Map<Sector, List<Holding>> groupedHoldings = new HashMap<>();

        for (Holding holding : holdings) {
            groupedHoldings.computeIfAbsent(holding.getSector(), k -> new ArrayList<>()).add(holding);
        }

        return groupedHoldings;
    }

    @Override
    public List<Holding> findHoldingsByRiskLevel(String userId, String riskLevel) {
        // Simplified risk categorization based on gain/loss percentage volatility
        String jpql;
        switch (riskLevel.toLowerCase()) {
            case "low":
                jpql = """
                        SELECT h FROM Holding h 
                        WHERE h.portfolio.userId = :userId 
                        AND ABS(h.gainLossPercent) <= 10
                        ORDER BY h.gainLossPercent DESC
                        """;
                break;
            case "high":
                jpql = """
                        SELECT h FROM Holding h 
                        WHERE h.portfolio.userId = :userId 
                        AND ABS(h.gainLossPercent) > 25
                        ORDER BY h.gainLossPercent DESC
                        """;
                break;
            default: // medium
                jpql = """
                        SELECT h FROM Holding h 
                        WHERE h.portfolio.userId = :userId 
                        AND ABS(h.gainLossPercent) > 10 AND ABS(h.gainLossPercent) <= 25
                        ORDER BY h.gainLossPercent DESC
                        """;
        }

        TypedQuery<Holding> query = entityManager.createQuery(jpql, Holding.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    public BigDecimal calculatePortfolioDiversificationScore(String portfolioId) {
        // Simple diversification score based on sector distribution
        String jpql = """
                SELECT COUNT(DISTINCT h.sector), COUNT(h) 
                FROM Holding h 
                WHERE h.portfolio.id = :portfolioId
                """;

        TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);
        query.setParameter("portfolioId", portfolioId);

        Object[] result = query.getSingleResult();
        Long distinctSectors = (Long) result[0];
        Long totalHoldings = (Long) result[1];

        if (totalHoldings == 0) {
            return BigDecimal.ZERO;
        }

        // Simple score: (distinct sectors / total holdings) * 100
        return BigDecimal.valueOf(distinctSectors.doubleValue() / totalHoldings.doubleValue() * 100);
    }

    @Override
    public List<Object[]> getPerformanceComparison(String symbol, String userId) {
        String jpql = """
                SELECT h.portfolio.name, h.gainLossPercent, h.currentValue, h.purchaseDate
                FROM Holding h 
                WHERE h.symbol = :symbol 
                AND h.portfolio.userId = :userId
                ORDER BY h.gainLossPercent DESC
                """;

        TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);
        query.setParameter("symbol", symbol);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
}