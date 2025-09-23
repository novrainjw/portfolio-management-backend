package com.portfolio.management.repository.specification;

import com.portfolio.management.entity.Watchlist;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;

public class WatchlistSpecifications {
    public static Specification<Watchlist> belongsToUser(String userId) {
        return (root, query, criteriaBuilder) -> {
            if (userId == null || userId.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("user").get("id"), userId);
        };
    }

    public static Specification<Watchlist> hasSymbol(String symbol) {
        return (root, query, criteriaBuilder) -> {
            if (symbol == null || symbol.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("symbol")),
                    "%" + symbol.toLowerCase() + "%");
        };
    }

    public static Specification<Watchlist> hasCompanyName(String companyName) {
        return (root, query, criteriaBuilder) -> {
            if (companyName == null || companyName.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("companyName")),
                    "%" + companyName.toLowerCase() + "%");
        };
    }

    public static Specification<Watchlist> currentPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null && maxPrice == null) {
                return criteriaBuilder.conjunction();
            }
            if (minPrice != null && maxPrice != null) {
                return criteriaBuilder.between(root.get("currentPrice"), minPrice, maxPrice);
            }
            if (minPrice != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("currentPrice"), minPrice);
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("currentPrice"), maxPrice);
        };
    }

    public static Specification<Watchlist> changePercentBetween(BigDecimal minPercent, BigDecimal maxPercent) {
        return (root, query, criteriaBuilder) -> {
            if (minPercent == null && maxPercent == null) {
                return criteriaBuilder.conjunction();
            }
            if (minPercent != null && maxPercent != null) {
                return criteriaBuilder.between(root.get("changePercent"), minPercent, maxPercent);
            }
            if (minPercent != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("changePercent"), minPercent);
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("changePercent"), maxPercent);
        };
    }

    public static Specification<Watchlist> addedBetween(Instant startDate, Instant endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null && endDate == null) {
                return criteriaBuilder.conjunction();
            }
            if (startDate != null && endDate != null) {
                return criteriaBuilder.between(root.get("addedDate"), startDate, endDate);
            }
            if (startDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("addedDate"), startDate);
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("addedDate"), endDate);
        };
    }

    public static Specification<Watchlist> isGainer() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get("changePercent"), BigDecimal.ZERO);
    }

    public static Specification<Watchlist> isLoser() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThan(root.get("changePercent"), BigDecimal.ZERO);
    }

    public static Specification<Watchlist> isUnchanged() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("changePercent"), BigDecimal.ZERO);
    }

    public static Specification<Watchlist> isRecentlyAdded(Instant since) {
        return (root, query, criteriaBuilder) -> {
            if (since == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("addedDate"), since);
        };
    }

    public static Specification<Watchlist> searchByTerm(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (searchTerm == null || searchTerm.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String pattern = "%" + searchTerm.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("symbol")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("companyName")), pattern)
            );
        };
    }

    public static Specification<Watchlist> priceAboveThreshold(BigDecimal threshold) {
        return (root, query, criteriaBuilder) -> {
            if (threshold == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("currentPrice"), threshold);
        };
    }

    public static Specification<Watchlist> changeAboveThreshold(BigDecimal threshold) {
        return (root, query, criteriaBuilder) -> {
            if (threshold == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("changePercent"), threshold);
        };
    }
}
