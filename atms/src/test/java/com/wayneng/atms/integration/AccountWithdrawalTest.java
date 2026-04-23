package com.wayneng.atms.integration;

import com.wayneng.atms.model.*;
import com.wayneng.atms.repository.*;
import com.wayneng.atms.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AccountWithdrawalTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BankRepository bankRepository;

    private static final String ACCOUNT_NUMBER = "ACC123";

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
        customerRepository.deleteAll();
        bankRepository.deleteAll();

        Bank bank = new Bank();
        bank.setBankCode("BANK001");
        bank.setName("Test Bank");
        bankRepository.save(bank);

        Customer customer = new Customer();
        customer.setFullName("John Doe");
        customer.setCustomerNumber("CUS001");
        customer.setEmail("john@test.com");
        customer.setStatus("ACTIVE");
        customer.setBank(bank);
        customerRepository.save(customer);

        Account account = new Account();
        account.setAccountNumber(ACCOUNT_NUMBER);
        account.setAccountType("SAVINGS");
        account.setCurrency("MYR");
        account.setAccountStatus("ACTIVE");
        account.setAvailableBalance(new BigDecimal("1000.00"));
        account.setLedgerBalance(new BigDecimal("1000.00"));
        account.setMinimumBalance(BigDecimal.ZERO);
        account.setDailyWithdrawalLimit(null);
        account.setCustomer(customer);
        account.setBank(bank);
        accountRepository.save(account);
    }

    // SUCCESS
    @Test
    void successfulWithdrawal_shouldReduceBalance() {
        accountService.withdraw(ACCOUNT_NUMBER, new BigDecimal("100.00"));

        Account updated = accountRepository.findById(ACCOUNT_NUMBER).orElseThrow();

        assertThat(updated.getAvailableBalance())
                .isEqualByComparingTo("900.00");

        assertThat(updated.getLedgerBalance())
                .isEqualByComparingTo("900.00");
    }

    // FAIL - BELOW MINIMUM
    @Test
    void withdrawal_belowMinimum_shouldFail() {
        assertThatThrownBy(() ->
                accountService.withdraw(ACCOUNT_NUMBER, new BigDecimal("5.00"))
        ).hasMessage("Minimum withdrawal amount is 10");
    }

    // FAIL - ACCOUNT NOT FOUND
    @Test
    void withdrawal_accountNotFound_shouldFail() {
        assertThatThrownBy(() ->
                accountService.withdraw("NON_EXISTENT", new BigDecimal("100.00"))
        ).hasMessage("Account not found or inactive");
    }

    // FAIL - ACCOUNT INACTIVE
    @Test
    void withdrawal_accountInactive_shouldFail() {
        Account account = accountRepository.findById(ACCOUNT_NUMBER).orElseThrow();
        account.setAccountStatus("INACTIVE");
        accountRepository.save(account);

        assertThatThrownBy(() ->
                accountService.withdraw(ACCOUNT_NUMBER, new BigDecimal("100.00"))
        ).hasMessage("Account not found or inactive");
    }

    // FAIL - INSUFFICIENT BALANCE
    @Test
    void withdrawal_insufficientBalance_shouldFail() {
        assertThatThrownBy(() ->
                accountService.withdraw(ACCOUNT_NUMBER, new BigDecimal("2000.00"))
        ).hasMessage("Insufficient balance");
    }

    // FAIL - MINIMUM BALANCE VIOLATION
    @Test
    void withdrawal_minimumBalanceViolated_shouldFail() {
        Account account = accountRepository.findById(ACCOUNT_NUMBER).orElseThrow();
        account.setMinimumBalance(new BigDecimal("950.00"));
        accountRepository.save(account);

        assertThatThrownBy(() ->
                accountService.withdraw(ACCOUNT_NUMBER, new BigDecimal("100.00"))
        ).hasMessage("Minimum balance violated");
    }

    // FAIL - DAILY LIMIT EXCEEDED
    @Test
    void withdrawal_dailyLimitExceeded_shouldFail() {
        Account account = accountRepository.findById(ACCOUNT_NUMBER).orElseThrow();
        account.setDailyWithdrawalLimit(new BigDecimal("50.00"));
        accountRepository.save(account);

        assertThatThrownBy(() ->
                accountService.withdraw(ACCOUNT_NUMBER, new BigDecimal("100.00"))
        ).hasMessage("Daily withdrawal limit exceeded");
    }
}