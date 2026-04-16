package com.wayneng.atms.repository;

import com.wayneng.atms.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findTop10ByAccountAccountNumberOrderByTransactionTimeDesc(String accountNumber);
}