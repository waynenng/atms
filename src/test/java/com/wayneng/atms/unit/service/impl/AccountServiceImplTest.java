package com.wayneng.atms.unit.service.impl;

import com.wayneng.atms.model.Account;
import com.wayneng.atms.repository.AccountRepository;
import com.wayneng.atms.repository.TransactionRepository;
import com.wayneng.atms.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setAccountNumber("1111122222");
        account.setAccountType("SAVINGS");
        account.setCurrency("MYR");
        account.setAccountStatus("ACTIVE");
        account.setAvailableBalance(new BigDecimal("5000.00"));
        account.setLedgerBalance(new BigDecimal("5000.00"));
        account.setMinimumBalance(new BigDecimal("100.00"));
        account.setDailyWithdrawalLimit(new BigDecimal("10000.00"));
    }

    // getActiveAccount
    @Test
    void shouldReturnActiveAccountSuccessfully() {
        when(accountRepository.findByAccountNumberAndAccountStatus("1111122222", "ACTIVE"))
                .thenReturn(Optional.of(account));

        Account result = accountService.getActiveAccount("1111122222");

        assertNotNull(result);
        assertEquals("1111122222", result.getAccountNumber());
    }

    @Test
    void shouldThrowException_whenAccountNotFoundOrInactive() {
        when(accountRepository.findByAccountNumberAndAccountStatus("1111122222", "ACTIVE"))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> accountService.getActiveAccount("1111122222"));

        assertEquals("Account not found or inactive", ex.getMessage());
    }

    // getBalance
    @Test
    void shouldReturnBalanceSuccessfully() {
        when(accountRepository.findByAccountNumberAndAccountStatus("1111122222", "ACTIVE"))
                .thenReturn(Optional.of(account));

        BigDecimal result = accountService.getBalance("1111122222");

        assertEquals(new BigDecimal("5000.00"), result);
    }

    // deposit
    @Test
    void shouldDepositSuccessfully() {
        when(accountRepository.findByAccountNumberAndAccountStatus("1111122222", "ACTIVE"))
                .thenReturn(Optional.of(account));

        accountService.deposit("1111122222", new BigDecimal("1000.00"));

        assertEquals(new BigDecimal("6000.00"), account.getAvailableBalance());
        assertEquals(new BigDecimal("6000.00"), account.getLedgerBalance());
        verify(accountRepository).save(account);
    }

    @Test
    void shouldAllowDeposit_whenAmountEqualsMinimum() {
        when(accountRepository.findByAccountNumberAndAccountStatus("1111122222", "ACTIVE"))
                .thenReturn(Optional.of(account));

        accountService.deposit("1111122222", BigDecimal.TEN);

        verify(accountRepository).save(account);
    }

    @Test
    void shouldThrowException_whenDepositAmountBelowMinimum() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> accountService.deposit("1111122222", new BigDecimal("5.00")));

        assertEquals("Minimum deposit amount is 10", ex.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void shouldThrowException_whenDepositAccountNotFound() {
        when(accountRepository.findByAccountNumberAndAccountStatus("1111122222", "ACTIVE"))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> accountService.deposit("1111122222", new BigDecimal("1000.00")));

        assertEquals("Account not found or inactive", ex.getMessage());
        verify(accountRepository, never()).save(any());
    }

    // withdraw
    @Test
    void shouldWithdrawSuccessfully() {
        when(accountRepository.findByAccountNumberAndAccountStatus("1111122222", "ACTIVE"))
                .thenReturn(Optional.of(account));

        when(transactionRepository.sumWithdrawalsToday(eq("1111122222"), any(LocalDateTime.class)))
                .thenReturn(BigDecimal.ZERO);

        accountService.withdraw("1111122222", new BigDecimal("1000.00"));

        assertEquals(new BigDecimal("4000.00"), account.getAvailableBalance());
        assertEquals(new BigDecimal("4000.00"), account.getLedgerBalance());
        verify(accountRepository).save(account);
    }

    @Test
    void shouldThrowException_whenWithdrawAccountNotFound() {
        when(accountRepository.findByAccountNumberAndAccountStatus("1111122222", "ACTIVE"))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> accountService.withdraw("1111122222", new BigDecimal("1000.00")));

        assertEquals("Account not found or inactive", ex.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void shouldThrowException_whenWithdrawalAmountBelowMinimum() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> accountService.withdraw("1111122222", new BigDecimal("9.99")));

        assertEquals("Minimum withdrawal amount is 10", ex.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void shouldAllowWithdrawal_whenAmountEqualsMinimum() {
        when(accountRepository.findByAccountNumberAndAccountStatus("1111122222", "ACTIVE"))
                .thenReturn(Optional.of(account));

        when(transactionRepository.sumWithdrawalsToday(any(), any()))
                .thenReturn(BigDecimal.ZERO);

        accountService.withdraw("1111122222", BigDecimal.TEN);

        verify(accountRepository).save(account);
    }

    @Test
    void shouldThrowException_whenInsufficientBalance() {
        when(accountRepository.findByAccountNumberAndAccountStatus("1111122222", "ACTIVE"))
                .thenReturn(Optional.of(account));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> accountService.withdraw("1111122222", new BigDecimal("6000.00")));

        assertEquals("Insufficient balance", ex.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void shouldThrowException_whenMinimumBalanceViolated() {
        when(accountRepository.findByAccountNumberAndAccountStatus("1111122222", "ACTIVE"))
                .thenReturn(Optional.of(account));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> accountService.withdraw("1111122222", new BigDecimal("4900.01")));

        assertEquals("Minimum balance violated", ex.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void shouldIgnoreMinimumBalance_whenNull() {
        account.setMinimumBalance(null);

        when(accountRepository.findByAccountNumberAndAccountStatus("1111122222", "ACTIVE"))
                .thenReturn(Optional.of(account));

        when(transactionRepository.sumWithdrawalsToday(any(), any()))
                .thenReturn(BigDecimal.ZERO);

        accountService.withdraw("1111122222", new BigDecimal("4900.01"));

        assertEquals(new BigDecimal("99.99"), account.getAvailableBalance());
        assertEquals(new BigDecimal("99.99"), account.getLedgerBalance());
        verify(accountRepository).save(account);
    }

    @Test
    void shouldThrowException_whenDailyWithdrawalLimitExceeded() {
        account.setAvailableBalance(new BigDecimal("11000.00"));
        account.setLedgerBalance(new BigDecimal("11000.00"));

        when(accountRepository.findByAccountNumberAndAccountStatus("1111122222", "ACTIVE"))
                .thenReturn(Optional.of(account));

        when(transactionRepository.sumWithdrawalsToday(any(), any()))
                .thenReturn(new BigDecimal("5000.00"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> accountService.withdraw("1111122222", new BigDecimal("5000.01")));

        assertEquals("Daily withdrawal limit exceeded", ex.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void shouldIgnoreDailyLimit_whenNull() {
        account.setDailyWithdrawalLimit(null);
        account.setAvailableBalance(new BigDecimal("11000.00"));
        account.setLedgerBalance(new BigDecimal("11000.00"));

        when(accountRepository.findByAccountNumberAndAccountStatus("1111122222", "ACTIVE"))
                .thenReturn(Optional.of(account));

        accountService.withdraw("1111122222", new BigDecimal("10000.01"));

        verify(accountRepository).save(account);
    }
}