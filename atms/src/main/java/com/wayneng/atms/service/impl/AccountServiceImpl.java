package com.wayneng.atms.service.impl;

import com.wayneng.atms.model.Account;
import com.wayneng.atms.repository.AccountRepository;
import com.wayneng.atms.service.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
public class AccountServiceImpl implements AccountService {

    // APPROVED
    private final AccountRepository accountRepository;

    // APPROVED
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // APPROVED
    private Account getActiveAccount(String accountNumber) {
        return accountRepository
            .findByAccountNumberAndAccountStatus(accountNumber, "ACTIVE")
            .orElseThrow(() -> new RuntimeException("Account not found or inactive"));
    }

    // APPROVED
    @Override
    public BigDecimal getBalance(String accountNumber) {
        Account account = getActiveAccount(accountNumber);
        return account.getAvailableBalance();
    }

    // APPROVED
    @Override
    @Transactional
    public void deposit(String accountNumber, BigDecimal amount) {

        if (amount.compareTo(BigDecimal.TEN) < 0) {
            throw new RuntimeException("Minimum deposit amount is 10");
        }

        Account account = getActiveAccount(accountNumber);

        account.setAvailableBalance(
            account.getAvailableBalance().add(amount)
        );

        account.setLedgerBalance(
            account.getLedgerBalance().add(amount)
        );

        accountRepository.save(account);
    }

    // PENDING
    @Override
    @Transactional
    public void withdraw(String accountNumber, BigDecimal amount) {

        if (amount.compareTo(BigDecimal.TEN) < 0) {
            throw new RuntimeException("Minimum withdrawal amount is 10");
        }

        Account account = getActiveAccount(accountNumber);

        BigDecimal newBalance = account.getAvailableBalance().subtract(amount);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        if (account.getMinimumBalance() != null &&
            newBalance.compareTo(account.getMinimumBalance()) < 0) {
            throw new RuntimeException("Minimum balance violated");
        }

        if (account.getDailyWithdrawalLimit() != null &&
            amount.compareTo(account.getDailyWithdrawalLimit()) > 0) {
            throw new RuntimeException("Daily withdrawal limit exceeded");
        }

        account.setAvailableBalance(newBalance);
        account.setLedgerBalance(account.getLedgerBalance().subtract(amount));

        accountRepository.save(account);
    }
}