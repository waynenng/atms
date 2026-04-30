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
        assertEquals("ACTIVE", result.getAccountStatus());
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

        accountService.getBalance("1111122222");

        assertEquals(new BigDecimal("5000.00"), account.getAvailableBalance());
    }

    // deposit
    @Test
    void shouldDepositSuccessfully() {
        when(accountRepository.findByAccountNumberAndAccountStatus("1111122222", "ACTIVE"))
                .thenReturn(Optional.of(account));

        accountService.deposit("1111122222", new BigDecimal("1000.00"));

        assertEquals(account.getAvailableBalance(), new BigDecimal("6000.00"));
        assertEquals(account.getLedgerBalance(), new BigDecimal("6000.00"));
        verify(accountRepository).save(account);
    }

    @Test
    void shouldThrowException_whenDepositAmountBelowMinimum() {

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> accountService.deposit("1111122222", new BigDecimal("5.00")));

        assertEquals("Minimum deposit amount is 10", ex.getMessage());
    }

    // withdraw
    @Test
    void shouldWithdrawSuccessfully(){
        when(accountRepository.findByAccountNumberAndAccountStatus("1111122222", "ACTIVE"))
                .thenReturn(Optional.of(account));
        when(transactionRepository.sumWithdrawalsToday("1111122222", LocalDateTime.parse("2026-04-30T00:00")))
                .thenReturn(new BigDecimal("0.00"));

        accountService.withdraw("1111122222", new BigDecimal("1000.00"));

        assertEquals(new BigDecimal("4000.00"), account.getAvailableBalance());
        assertEquals(new BigDecimal("4000.00"), account.getLedgerBalance());
        verify(accountRepository).save(account);
    }
}
