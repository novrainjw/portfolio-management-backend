package com.portfolio.management.repository.custom.impl;

import com.portfolio.management.entity.Portfolio;
import com.portfolio.management.enums.Currency;
import com.portfolio.management.enums.Sector;
import com.portfolio.management.repository.custom.PortfolioRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;

@Repository
public class PortfolioRepositoryImpl implements PortfolioRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Portfolio> findPortfoliosWithDiversification(int minHoldings, int minSectors) {
        String jpql = """
                SELECT p FROM Portfolio p
                WHERE p.isActive = true
                AND (SELECT COUNT(h) FROM Holding h WHERE h.portfolio.id = p.id) >= :minHoldings
                AND (SELECT COUNT(DISTINCT h.sector) FROM Holding h WHERE h.portfolio.id = p.id) >= :minSectors
                """;

        TypedQuery<Portfolio> query = entityManager.createQuery(jpql, Portfolio.class);
        query.setParameter("minHoldings", minHoldings);
        query.setParameter("minSectors", minSectors);
        return query.getResultList();
    }

    @Override
    public Map<Sector, BigDecimal> getPortfolioSectorAllocation(String portfolioId) {
        String jpql = """
                SELECT h.sector, SUM(h.currentValue) 
                FROM Holding h 
                WHERE h.portfolio.id = :portfolioId 
                GROUP BY h.sector
                """;

        TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);
        query.setParameter("portfolioId", portfolioId);

        Map<Sector, BigDecimal> allocation = new HashMap<>();
        List<Object[]> results = query.getResultList();

        for (Object[] result : results) {
            Sector sector = (Sector) result[0];
            BigDecimal value = (BigDecimal) result[1];
            allocation.put(sector, value);
        }

        return allocation;
    }

    @Override
    public List<Portfolio> findSimilarPortfolios(String portfolioId, double similarityThreshold) {
        // This is a simplified similarity check based on common holdings
        String jpql = """
                SELECT DISTINCT p FROM Portfolio p
                WHERE p.id != :portfolioId
                AND p.isActive = true
                AND (
                    SELECT COUNT(h1.symbol) 
                    FROM Holding h1 
                    WHERE h1.portfolio.id = p.id
                    AND h1.symbol IN (
                        SELECT h2.symbol 
                        FROM Holding h2 
                        WHERE h2.portfolio.id = :portfolioId
                    )
                ) >= :minCommonHoldings
                """;

        TypedQuery<Portfolio> query = entityManager.createQuery(jpql, Portfolio.class);
        query.setParameter("portfolioId", portfolioId);
        query.setParameter("minCommonHoldings", (int) Math.ceil(similarityThreshold * 10)); // Simplified calculation

        return query.getResultList();
    }

    @Override
    public List<Portfolio> findPortfoliosByComplexCriteria(String userId, BigDecimal minValue,
                                                           BigDecimal maxValue, Currency currency,
                                                           String broker, Boolean isActive) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Portfolio> query = cb.createQuery(Portfolio.class);
        Root<Portfolio> portfolio = query.from(Portfolio.class);

        List<Predicate> predicates = new ArrayList<>();

        if (userId != null) {
            predicates.add(cb.equal(portfolio.get("user").get("id"), userId));
        }

        if (minValue != null) {
            predicates.add(cb.greaterThanOrEqualTo(portfolio.get("totalValue"), minValue));
        }

        if (maxValue != null) {
            predicates.add(cb.lessThanOrEqualTo(portfolio.get("totalValue"), maxValue));
        }

        if (currency != null) {
            predicates.add(cb.equal(portfolio.get("currency"), currency));
        }

        if (broker != null && !broker.isEmpty()) {
            predicates.add(cb.equal(portfolio.get("broker"), broker));
        }

        if (isActive != null) {
            predicates.add(cb.equal(portfolio.get("isActive"), isActive));
        }

        query.select(portfolio)
                .where(predicates.toArray(new Predicate[0]))
                .orderBy(cb.desc(portfolio.get("totalValue")));

        return entityManager.createQuery(query).getResultList();
    }
}
