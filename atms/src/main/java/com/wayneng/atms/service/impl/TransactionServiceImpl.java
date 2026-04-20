package com.wayneng.atms.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import com.wayneng.atms.model.ATM;
import com.wayneng.atms.model.Account;
import com.wayneng.atms.model.Card;
import com.wayneng.atms.model.Session;
import com.wayneng.atms.model.Transaction;
import com.wayneng.atms.repository.TransactionRepository;
import com.wayneng.atms.service.ATMService;
import com.wayneng.atms.service.AccountService;
import com.wayneng.atms.service.CardService;
import com.wayneng.atms.service.SessionService;
import com.wayneng.atms.service.TransactionService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final CardService cardService;
    private final SessionService sessionService;
    private final ATMService atmService;

    // APPROVED
    @Override
    public Transaction createTransaction(
            String transactionType,
            BigDecimal amount,
            String description,
            String accountNumber,
            String cardNumber,
            String sessionId,
            String atmCode) {

        Account account = accountService.getActiveAccount(accountNumber);
        Card card = cardService.getCardByNumber(cardNumber);
        Session session = sessionService.getSession(sessionId);
        ATM atm = atmService.getATMByCode(atmCode);

        Transaction transaction = new Transaction();
        transaction.setTransactionType(transactionType);
        transaction.setTransactionStatus("PENDING");
        transaction.setTransactionTime(LocalDateTime.now());
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setAccount(account);
        transaction.setCard(card);
        transaction.setSession(session);
        transaction.setAtm(atm);

        return transactionRepository.save(transaction);
    }

    // APPROVED
    @Override
    public void updateTransactionStatus(String transactionId, String transactionStatus) {
        Transaction transaction = getTransactionById(transactionId);
        transaction.setTransactionStatus(transactionStatus);
        transactionRepository.save(transaction);
    }

    // APPROVED
    @Override
    public Transaction getTransactionById(String transactionId) {
        return transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }
}
