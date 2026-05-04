package com.wayneng.atms.service.impl;

import com.wayneng.atms.model.Session;
import com.wayneng.atms.model.Transaction;
import com.wayneng.atms.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DepositServiceImpl implements DepositService{

    private final CardService cardService;
    private final SessionService sessionService;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final ATMService atmService;

    @Override
    @Transactional
    public void deposit(String cardNumber, String pin, String atmCode, BigDecimal amount) {

        boolean success = false;
        Transaction transaction = null;

        Session session = sessionService.startSession(cardNumber, atmCode);

        String accountNumber = session.getCard().getAccount().getAccountNumber();

        transaction = transactionService.createTransaction(
                "DEPOSIT",
                amount,
                accountNumber,
                cardNumber,
                session.getSessionId(),
                atmCode
        );

        try {

            boolean validPin = cardService.validatePin(cardNumber, pin);

            if (!validPin) {
                sessionService.recordFailedPin(session.getSessionId());
                throw new RuntimeException("Invalid PIN");
            }

            sessionService.authenticateSession(session.getSessionId());

            accountService.deposit(accountNumber, amount);

            atmService.addCash(atmCode, amount);

            transactionService.updateTransactionStatus(
                    transaction.getTransactionId(),
                    "SUCCESS"
            );

            success = true;

        } catch (Exception e) {

            if (transaction != null) {
                transactionService.updateTransactionStatus(
                        transaction.getTransactionId(),
                        "FAILED"
                );
            }

            throw e;

        } finally {

            sessionService.endSession(
                    session.getSessionId(),
                    success ? "COMPLETED" : "FAILED"
            );
        }
    }
}