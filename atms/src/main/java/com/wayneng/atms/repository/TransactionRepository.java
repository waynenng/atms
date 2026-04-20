package com.wayneng.atms.repository;

import com.wayneng.atms.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionId(String transactionId);

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        JOIN t.account a
        WHERE a.accountNumber = :accountNumber
        AND t.transactionType = 'WITHDRAWAL'
        AND t.transactionStatus = 'SUCCESS'
        AND t.transactionTime >= :startOfDay
    """)
    BigDecimal sumWithdrawalsToday(String accountNumber, LocalDateTime startOfDay);
}