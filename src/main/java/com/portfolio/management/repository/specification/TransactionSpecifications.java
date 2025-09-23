package com.portfolio.management.repository.specification;

import com.portfolio.management.entity.Transaction;
import com.portfolio.management.enums.Currency;
import com.portfolio.management.enums.TransactionType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;

public class TransactionSpecifications {
    public static Specification<Transaction> belongsToPortfolio(String portfolioId) {
        return (root, query, criteriaBuilder) -> {
            if (portfolioId == null || portfolioId.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("portfolio").get("id"), portfolioId);
        };
    }

    public static Specification<Transaction> belongsToUser(String userId) {
        return (root, query, criteriaBuilder) -> {
            if (userId == null || userId.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("portfolio").get("user").get("id"), userId);
        };
    }

    public static Specification<Transaction> belongsToHolding(String holdingId) {
        return (root, query, criteriaBuilder) -> {
            if (holdingId == null || holdingId.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("holding").get("id"), holdingId);
        };
    }

    public static Specification<Transaction> hasType(TransactionType type) {
        return (root, query, criteriaBuilder) -> {
            if (type == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("type"), type);
        };
    }

    public static Specification<Transaction> hasSymbol(String symbol) {
        return (root, query, criteriaBuilder) -> {
            if (symbol == null || symbol.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("symbol")),
                    "%" + symbol.toLowerCase() + "%");
        };
    }

    public static Specification<Transaction> hasCurrency(Currency currency) {
        return (root, query, criteriaBuilder) -> {
            if (currency == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("currency"), currency);
        };
    }

    public static Specification<Transaction> totalAmountBetween(BigDecimal minAmount, BigDecimal maxAmount) {
        return (root, query, criteriaBuilder) -> {
            if (minAmount == null && maxAmount == null) {
                return criteriaBuilder.conjunction();
            }
            if (minAmount != null && maxAmount != null) {
                return criteriaBuilder.between(root.get("totalAmount"), minAmount, maxAmount);
            }
            if (minAmount != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("totalAmount"), minAmount);
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("totalAmount"), maxAmount);
        };
    }

    public static Specification<Transaction> quantityBetween(BigDecimal minQuantity, BigDecimal maxQuantity) {
        return (root, query, criteriaBuilder) -> {
            if (minQuantity == null && maxQuantity == null) {
                return criteriaBuilder.conjunction();
            }
            if (minQuantity != null && maxQuantity != null) {
                return criteriaBuilder.between(root.get("quantity"), minQuantity, maxQuantity);
            }
            if (minQuantity != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("quantity"), minQuantity);
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("quantity"), maxQuantity);
        };
    }

    public static Specification<Transaction> priceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null && maxPrice == null) {
                return criteriaBuilder.conjunction();
            }
            if (minPrice != null && maxPrice != null) {
                return criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
            }
            if (minPrice != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
        };
    }

    public static Specification<Transaction> feesBetween(BigDecimal minFees, BigDecimal maxFees) {
        return (root, query, criteriaBuilder) -> {
            if (minFees == null && maxFees == null) {
                return criteriaBuilder.conjunction();
            }
            if (minFees != null && maxFees != null) {
                return criteriaBuilder.between(root.get("fees"), minFees, maxFees);
            }
            if (minFees != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("fees"), minFees);
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("fees"), maxFees);
        };
    }

    public static Specification<Transaction> transactionDateBetween(Instant startDate, Instant endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null && endDate == null) {
                return criteriaBuilder.conjunction();
            }
            if (startDate != null && endDate != null) {
                return criteriaBuilder.between(root.get("transactionDate"), startDate, endDate);
            }
            if (startDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("transactionDate"), startDate);
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("transactionDate"), endDate);
        };
    }

    public static Specification<Transaction> hasNotes() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.isNotNull(root.get("notes")),
                        criteriaBuilder.notEqual(root.get("notes"), "")
                );
    }

    public static Specification<Transaction> notesContain(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (searchTerm == null || searchTerm.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("notes")),
                    "%" + searchTerm.toLowerCase() + "%");
        };
    }

    public static Specification<Transaction> isHighValue(BigDecimal threshold) {
        return (root, query, criteriaBuilder) -> {
            if (threshold == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("totalAmount"), threshold);
        };
    }

    public static Specification<Transaction> isRecentTransaction(Instant since) {
        return (root, query, criteriaBuilder) -> {
            if (since == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("transactionDate"), since);
        };
    }
}
