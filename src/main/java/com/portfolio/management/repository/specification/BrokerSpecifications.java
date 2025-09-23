package com.portfolio.management.repository.specification;

import com.portfolio.management.entity.Broker;
import com.portfolio.management.enums.BrokerType;
import com.portfolio.management.enums.Country;
import com.portfolio.management.enums.Currency;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class BrokerSpecifications {
    public static Specification<Broker> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                    "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Broker> hasType(BrokerType type) {
        return (root, query, criteriaBuilder) -> {
            if (type == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("type"), type);
        };
    }

    public static Specification<Broker> hasCountry(Country country) {
        return (root, query, criteriaBuilder) -> {
            if (country == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("country"), country);
        };
    }

    public static Specification<Broker> isActive(Boolean active) {
        return (root, query, criteriaBuilder) -> {
            if (active == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("isActive"), active);
        };
    }

    public static Specification<Broker> supportsCurrency(Currency currency) {
        return (root, query, criteriaBuilder) -> {
            if (currency == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.isMember(currency, root.get("supportedCurrencies"));
        };
    }

    public static Specification<Broker> tradingFeeBetween(BigDecimal minFee, BigDecimal maxFee) {
        return (root, query, criteriaBuilder) -> {
            if (minFee == null && maxFee == null) {
                return criteriaBuilder.conjunction();
            }
            if (minFee != null && maxFee != null) {
                return criteriaBuilder.between(root.get("tradingFee"), minFee, maxFee);
            }
            if (minFee != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("tradingFee"), minFee);
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("tradingFee"), maxFee);
        };
    }

    public static Specification<Broker> isCommissionFree() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("tradingFee"), BigDecimal.ZERO);
    }

    public static Specification<Broker> hasDescription() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.isNotNull(root.get("description")),
                        criteriaBuilder.notEqual(root.get("description"), "")
                );
    }

    public static Specification<Broker> hasWebsite() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.isNotNull(root.get("websiteUrl")),
                        criteriaBuilder.notEqual(root.get("websiteUrl"), "")
                );
    }

    public static Specification<Broker> descriptionContains(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                    "%" + keyword.toLowerCase() + "%");
        };
    }

    public static Specification<Broker> searchByTerm(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (searchTerm == null || searchTerm.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String pattern = "%" + searchTerm.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern)
            );
        };
    }
}
