package com.portfolio.management.repository.custom;

import com.portfolio.management.entity.User;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface UserRepositoryCustom {
    List<User> findUsersWithPortfolioValueAbove(BigDecimal minValue);

    List<User> findTopPerformingUsers(int limit);

    List<User> findUsersWithActivityBetween(Instant startDate, Instant endDate);

    List<User> searchUsers(String searchTerm);
}
