package com.portfolio.management.repository;

import com.portfolio.management.entity.Transaction;
import com.portfolio.management.enums.Currency;
import com.portfolio.management.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String>, JpaSpecificationExecutor<Transaction> {

    // Find by portfolio
    List<Transaction> findByPortfolioId(String portfolioId);

    Page<Transaction> findByPortfolioId(String portfolioId, Pageable pageable);

    List<Transaction> findByPortfolioIdOrderByTransactionDateDesc(String portfolioId);

    // Find by holding
    List<Transaction> findByHoldingId(String holdingId);

    List<Transaction> findByHoldingIdOrderByTransactionDateDesc(String holdingId);

    // Find by user (through portfolio)
    @Query("SELECT t FROM Transaction t WHERE t.portfolio.userId = :userId")
    List<Transaction> findByUserId(@Param("userId") String userId);

    @Query("SELECT t FROM Transaction t WHERE t.portfolio.userId = :userId ORDER BY t.transactionDate DESC")
    List<Transaction> findByUserIdOrderByTransactionDateDesc(@Param("userId") String userId);

    @Query("SELECT t FROM Transaction t WHERE t.portfolio.userId = :userId")
    Page<Transaction> findByUserId(@Param("userId") String userId, Pageable pageable);

    // Find by type
    List<Transaction> findByType(TransactionType type);

    List<Transaction> findByPortfolioIdAndType(String portfolioId, TransactionType type);

    @Query("SELECT t FROM Transaction t WHERE t.portfolio.userId = :userId AND t.type = :type")
    List<Transaction> findByUserIdAndType(@Param("userId") String userId, @Param("type") TransactionType type);

    // Find by symbol
    List<Transaction> findBySymbol(String symbol);

    List<Transaction> findBySymbolOrderByTransactionDateDesc(String symbol);

    List<Transaction> findByPortfolioIdAndSymbol(String portfolioId, String symbol);

    @Query("SELECT t FROM Transaction t WHERE t.portfolio.userId = :userId AND t.symbol = :symbol ORDER BY t.transactionDate DESC")
    List<Transaction> findByUserIdAndSymbol(@Param("userId") String userId, @Param("symbol") String symbol);

    // Find by currency
    List<Transaction> findByCurrency(Currency currency);

    List<Transaction> findByPortfolioIdAndCurrency(String portfolioId, Currency currency);

    // Find by date ranges
    List<Transaction> findByTransactionDateBetween(Instant startDate, Instant endDate);

    List<Transaction> findByTransactionDateAfter(Instant after);

    List<Transaction> findByTransactionDateBefore(Instant before);

    List<Transaction> findByPortfolioIdAndTransactionDateBetween(String portfolioId, Instant startDate, Instant endDate);

    @Query("SELECT t FROM Transaction t WHERE t.portfolio.userId = :userId AND t.transactionDate BETWEEN :startDate AND :endDate ORDER BY t.transactionDate DESC")
    List<Transaction> findByUserIdAndTransactionDateBetween(@Param("userId") String userId, @Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    // Find by amount ranges
    List<Transaction> findByTotalAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);

    List<Transaction> findByTotalAmountGreaterThan(BigDecimal minAmount);

    List<Transaction> findByFeesGreaterThan(BigDecimal minFees);

    // Recent transactions
    @Query("SELECT t FROM Transaction t WHERE t.transactionDate >= :since ORDER BY t.transactionDate DESC")
    List<Transaction> findRecentTransactions(@Param("since") Instant since);

    @Query("SELECT t FROM Transaction t WHERE t.portfolio.userId = :userId AND t.transactionDate >= :since ORDER BY t.transactionDate DESC")
    List<Transaction> findRecentTransactionsByUserId(@Param("userId") String userId, @Param("since") Instant since);

    // Large transactions
    @Query("SELECT t FROM Transaction t WHERE t.totalAmount >= :minAmount ORDER BY t.totalAmount DESC")
    List<Transaction> findLargeTransactions(@Param("minAmount") BigDecimal minAmount);

    @Query("SELECT t FROM Transaction t WHERE t.portfolio.userId = :userId ORDER BY t.totalAmount DESC")
    Page<Transaction> findLargestTransactionsByUserId(@Param("userId") String userId, Pageable pageable);

    // Buy/Sell transactions
    @Query("SELECT t FROM Transaction t WHERE t.type = 'BUY' AND t.portfolio.userId = :userId ORDER BY t.transactionDate DESC")
    List<Transaction> findBuyTransactionsByUserId(@Param("userId") String userId);

    @Query("SELECT t FROM Transaction t WHERE t.type = 'SELL' AND t.portfolio.userId = :userId ORDER BY t.transactionDate DESC")
    List<Transaction> findSellTransactionsByUserId(@Param("userId") String userId);

    @Query("SELECT t FROM Transaction t WHERE t.type = 'DIVIDEND' AND t.portfolio.userId = :userId ORDER BY t.transactionDate DESC")
    List<Transaction> findDividendTransactionsByUserId(@Param("userId") String userId);

    // Transaction statistics
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.portfolio.id = :portfolioId")
    long countByPortfolioId(@Param("portfolioId") String portfolioId);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.portfolio.userId = :userId")
    long countByUserId(@Param("userId") String userId);

    @Query("SELECT SUM(t.totalAmount) FROM Transaction t WHERE t.portfolio.id = :portfolioId AND t.type = :type")
    Optional<BigDecimal> getTotalAmountByPortfolioIdAndType(@Param("portfolioId") String portfolioId, @Param("type") TransactionType type);

    @Query("SELECT SUM(t.totalAmount) FROM Transaction t WHERE t.portfolio.userId = :userId AND t.type = :type")
    Optional<BigDecimal> getTotalAmountByUserIdAndType(@Param("userId") String userId, @Param("type") TransactionType type);

    @Query("SELECT SUM(t.fees) FROM Transaction t WHERE t.portfolio.userId = :userId")
    Optional<BigDecimal> getTotalFeesByUserId(@Param("userId") String userId);

    // Transaction counts by type
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.portfolio.userId = :userId AND t.type = 'BUY'")
    long countBuyTransactionsByUserId(@Param("userId") String userId);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.portfolio.userId = :userId AND t.type = 'SELL'")
    long countSellTransactionsByUserId(@Param("userId") String userId);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.portfolio.userId = :userId AND t.type = 'DIVIDEND'")
    long countDividendTransactionsByUserId(@Param("userId") String userId);

    // Symbol-based statistics
    @Query("SELECT COUNT(DISTINCT t.symbol) FROM Transaction t WHERE t.portfolio.userId = :userId")
    long countDistinctSymbolsByUserId(@Param("userId") String userId);

    @Query("SELECT t.symbol, COUNT(t) FROM Transaction t WHERE t.portfolio.userId = :userId GROUP BY t.symbol ORDER BY COUNT(t) DESC")
    List<Object[]> getTransactionCountBySymbolForUserId(@Param("userId") String userId);

    // Type statistics
    @Query("SELECT t.type, COUNT(t), SUM(t.totalAmount) FROM Transaction t WHERE t.portfolio.userId = :userId GROUP BY t.type")
    List<Object[]> getTransactionStatisticsByType(@Param("userId") String userId);

    // Currency statistics
    @Query("SELECT t.currency, COUNT(t), SUM(t.totalAmount) FROM Transaction t WHERE t.portfolio.userId = :userId GROUP BY t.currency")
    List<Object[]> getTransactionStatisticsByCurrency(@Param("userId") String userId);

    // Time-based analytics
    @Query("SELECT FUNCTION('DATE_TRUNC', 'month', t.transactionDate), COUNT(t), SUM(t.totalAmount) " +
            "FROM Transaction t WHERE t.portfolio.userId = :userId " +
            "GROUP BY FUNCTION('DATE_TRUNC', 'month', t.transactionDate) " +
            "ORDER BY FUNCTION('DATE_TRUNC', 'month', t.transactionDate) DESC")
    List<Object[]> getMonthlyTransactionStatistics(@Param("userId") String userId);

    @Query("SELECT FUNCTION('DATE_TRUNC', 'year', t.transactionDate), COUNT(t), SUM(t.totalAmount) " +
            "FROM Transaction t WHERE t.portfolio.userId = :userId " +
            "GROUP BY FUNCTION('DATE_TRUNC', 'year', t.transactionDate) " +
            "ORDER BY FUNCTION('DATE_TRUNC', 'year', t.transactionDate) DESC")
    List<Object[]> getYearlyTransactionStatistics(@Param("userId") String userId);

    // Portfolio activity
    @Query("SELECT t FROM Transaction t WHERE t.portfolio.id = :portfolioId ORDER BY t.transactionDate DESC")
    Page<Transaction> findByPortfolioIdOrderByTransactionDateDesc(@Param("portfolioId") String portfolioId, Pageable pageable);

    // Symbol transaction history
    @Query("SELECT t FROM Transaction t WHERE t.symbol = :symbol AND t.portfolio.userId = :userId ORDER BY t.transactionDate ASC")
    List<Transaction> findTransactionHistoryByUserIdAndSymbol(@Param("userId") String userId, @Param("symbol") String symbol);

    // Average transaction amounts
    @Query("SELECT AVG(t.totalAmount) FROM Transaction t WHERE t.portfolio.userId = :userId AND t.type = :type")
    Optional<BigDecimal> getAverageTransactionAmountByUserIdAndType(@Param("userId") String userId, @Param("type") TransactionType type);

    // First and last transactions
    @Query("SELECT t FROM Transaction t WHERE t.portfolio.userId = :userId ORDER BY t.transactionDate ASC")
    Page<Transaction> findFirstTransactionsByUserId(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.portfolio.userId = :userId ORDER BY t.transactionDate DESC")
    Page<Transaction> findLatestTransactionsByUserId(@Param("userId") String userId, Pageable pageable);

    // Transactions with notes
    @Query("SELECT t FROM Transaction t WHERE t.notes IS NOT NULL AND t.notes != ''")
    List<Transaction> findTransactionsWithNotes();

    @Query("SELECT t FROM Transaction t WHERE t.portfolio.userId = :userId AND t.notes IS NOT NULL AND t.notes != ''")
    List<Transaction> findTransactionsWithNotesByUserId(@Param("userId") String userId);

    // High-fee transactions
    @Query("SELECT t FROM Transaction t WHERE t.fees > :minFees ORDER BY t.fees DESC")
    List<Transaction> findHighFeeTransactions(@Param("minFees") BigDecimal minFees);

    // Transactions by holding and type
    @Query("SELECT t FROM Transaction t WHERE t.holding.id = :holdingId AND t.type = :type ORDER BY t.transactionDate DESC")
    List<Transaction> findByHoldingIdAndType(@Param("holdingId") String holdingId, @Param("type") TransactionType type);

    // Portfolio performance calculation support
    @Query("SELECT SUM(CASE WHEN t.type = 'BUY' THEN t.totalAmount ELSE -t.totalAmount END) " +
            "FROM Transaction t WHERE t.portfolio.id = :portfolioId AND t.symbol = :symbol")
    Optional<BigDecimal> getNetInvestmentByPortfolioIdAndSymbol(@Param("portfolioId") String portfolioId, @Param("symbol") String symbol);

    // Dividend income tracking
    @Query("SELECT SUM(t.totalAmount) FROM Transaction t WHERE t.portfolio.userId = :userId AND t.type = 'DIVIDEND' AND t.transactionDate BETWEEN :startDate AND :endDate")
    Optional<BigDecimal> getDividendIncomeByUserIdAndDateRange(@Param("userId") String userId, @Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    @Query("SELECT t.symbol, SUM(t.totalAmount) FROM Transaction t WHERE t.portfolio.userId = :userId AND t.type = 'DIVIDEND' GROUP BY t.symbol ORDER BY SUM(t.totalAmount) DESC")
    List<Object[]> getDividendIncomeBySymbolForUserId(@Param("userId") String userId);
}