package com.wayneng.atms.service;

import java.math.BigDecimal;
import com.wayneng.atms.model.Transaction;

public interface TransactionService {

    Transaction createTransaction(
        String transactionType,
        BigDecimal amount,
        String description,
        String accountNumber,
        String cardNumber,
        String sessionId,
        String atmCode
    );

    void updateTransactionStatus(String transactionId, String transactionType);

    Transaction getTransactionById(String transactionId);
}
