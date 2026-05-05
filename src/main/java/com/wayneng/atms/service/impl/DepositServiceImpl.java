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
public class DepositServiceImpl implements DepositService {

    private final AccountService accountService;
    private final TransactionService transactionService;
    private final ATMService atmService;

    @Override
    @Transactional
    public void deposit(Session session, BigDecimal amount) {

        String cardNumber = session.getCard().getCardNumber();

        String accountNumber = session.getCard().getAccount().getAccountNumber();

        String atmCode = session.getAtm().getAtmCode();

        Transaction transaction = null;

        try {

            transaction = transactionService.createTransaction(
                    "DEPOSIT",
                    amount,
                    accountNumber,
                    cardNumber,
                    session.getSessionId(),
                    atmCode
            );

            accountService.deposit(accountNumber, amount);

            atmService.addCash(atmCode, amount);

            transactionService.updateTransactionStatus(
                    transaction.getTransactionId(),
                    "SUCCESS"
            );

        } catch (Exception e) {

            if (transaction != null) {
                transactionService.updateTransactionStatus(
                        transaction.getTransactionId(),
                        "FAILED"
                );
            }

            throw e;
        }
    }
}