package com.portfolio.management.repository.custom.impl;

import com.portfolio.management.entity.User;
import com.portfolio.management.repository.custom.UserRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<User> findUsersWithPortfolioValueAbove(BigDecimal minValue) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> user = query.from(User.class);

        Subquery<BigDecimal> subquery = query.subquery(BigDecimal.class);
        Root<User> subUser = subquery.from(User.class);
        Join<Object, Object> portfolios = subUser.join("portfolios");

        subquery.select(cb.sum(portfolios.get("totalValue")))
                .where(cb.equal(subUser.get("id"), user.get("id")));

        query.select(user)
                .where(cb.greaterThan(subquery, minValue));

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<User> findTopPerformingUsers(int limit) {
        String jpql = """
                SELECT u FROM User u 
                LEFT JOIN u.portfolios p 
                WHERE p.isActive = true 
                GROUP BY u.id, u.username, u.firstName, u.lastName 
                ORDER BY AVG(p.totalGainLossPercent) DESC
                """;

        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public List<User> findUsersWithActivityBetween(Instant startDate, Instant endDate) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> user = query.from(User.class);
        Join<Object, Object> portfolios = user.join("portfolios", JoinType.LEFT);
        Join<Object, Object> transactions = portfolios.join("transactions", JoinType.LEFT);

        query.select(user).distinct(true)
                .where(cb.between(transactions.get("transactionDate"), startDate, endDate));

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<User> searchUsers(String searchTerm) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> user = query.from(User.class);

        String pattern = "%" + searchTerm.toLowerCase() + "%";

        Predicate usernamePredicate = cb.like(cb.lower(user.get("username")), pattern);
        Predicate emailPredicate = cb.like(cb.lower(user.get("email")), pattern);
        Predicate firstNamePredicate = cb.like(cb.lower(user.get("firstName")), pattern);
        Predicate lastNamePredicate = cb.like(cb.lower(user.get("lastName")), pattern);

        query.select(user)
                .where(cb.or(usernamePredicate, emailPredicate, firstNamePredicate, lastNamePredicate));

        return entityManager.createQuery(query).getResultList();
    }
}
