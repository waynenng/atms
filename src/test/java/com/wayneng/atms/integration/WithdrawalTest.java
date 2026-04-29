package com.wayneng.atms.integration;

import com.wayneng.atms.model.*;
import com.wayneng.atms.repository.*;
import com.wayneng.atms.service.WithdrawalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class WithdrawalTest {

    @Autowired private WithdrawalService withdrawalService;
    @Autowired private BankRepository bankRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private AccountRepository accountRepository;
    @Autowired private CardRepository cardRepository;
    @Autowired private ATMRepository atmRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private SessionRepository sessionRepository;
    @Autowired private BCryptPasswordEncoder passwordEncoder;

    private final String CARD_NUMBER = "1122334455667788";
    private final String PIN = "123456";
    private final String ATM_CODE = "SDCT001";
    private final String ACCOUNT_NUMBER = "1122334455";

    @BeforeEach
    void setup() {
        transactionRepository.deleteAll();
        sessionRepository.deleteAll();
        cardRepository.deleteAll();
        accountRepository.deleteAll();
        customerRepository.deleteAll();
        atmRepository.deleteAll();
        bankRepository.deleteAll();

        Bank bank = new Bank();
        bank.setBankCode("SDCT");
        bank.setName("Standard Chartered");
        bankRepository.save(bank);

        Customer customer = new Customer();
        customer.setFullName("Wayne Ng");
        customer.setEmail("ngzuwayne@gmail.com");
        customer.setStatus("ACTIVE");
        customer.setBank(bank);
        customerRepository.save(customer);

        Account account = new Account();
        account.setAccountNumber(ACCOUNT_NUMBER);
        account.setAccountType("SAVINGS");
        account.setCurrency("MYR");
        account.setAccountStatus("ACTIVE");
        account.setAvailableBalance(new BigDecimal("6000.00"));
        account.setLedgerBalance(new BigDecimal("6000.00"));
        account.setMinimumBalance(new BigDecimal("100.00"));
        account.setDailyWithdrawalLimit(new BigDecimal("5000.00"));
        account.setCustomer(customer);
        account.setBank(bank);
        accountRepository.save(account);

        Card card = new Card();
        card.setCardNumber(CARD_NUMBER);
        card.setCardType("VISA");
        card.setCardStatus("ACTIVE");
        card.setPinHash(passwordEncoder.encode(PIN));
        card.setExpiryDate(LocalDate.now().plusYears(3));
        card.setFailedPinAttempts(0);
        card.setCustomer(customer);
        card.setAccount(account);
        cardRepository.save(card);

        ATM atm = new ATM();
        atm.setAtmCode(ATM_CODE);
        atm.setLocationName("Happy Garden, KL");
        atm.setAtmStatus("ACTIVE");
        atm.setCashAvailable(new BigDecimal("3000.00"));
        atm.setCurrency("MYR");
        atm.setPerTransactionLimit(new BigDecimal("5000.00"));
        atm.setBank(bank);
        atmRepository.save(atm);
    }

    // SUCCESSFUL WITHDRAWAL
    @Test
    void shouldWithdrawSuccessfully() {

        BigDecimal withdrawAmount = new BigDecimal("200.00");

        withdrawalService.withdraw(CARD_NUMBER, PIN, ATM_CODE, withdrawAmount);

        Account updatedAccount = accountRepository.findById(ACCOUNT_NUMBER).orElseThrow();
        assertThat(updatedAccount.getAvailableBalance())
                .isEqualByComparingTo("5800.00");

        ATM updatedATM = atmRepository.findById(ATM_CODE).orElseThrow();
        assertThat(updatedATM.getCashAvailable())
                .isEqualByComparingTo("2800.00");

        Transaction tx = transactionRepository.findAll().get(0);
        assertThat(tx.getTransactionStatus()).isEqualTo("SUCCESS");
        assertThat(tx.getAmount()).isEqualByComparingTo("200.00");

        Session session = sessionRepository.findAll().get(0);
        assertThat(session.getSessionStatus()).isEqualTo("ENDED");
        assertThat(session.getAuthenticated()).isTrue();
    }

    // FAILURE - INVALID PIN
    @Test
    void shouldFailWithdrawal_invalidPin() {

        BigDecimal withdrawAmount = new BigDecimal("200.00");

        assertThatThrownBy(() ->
                withdrawalService.withdraw(CARD_NUMBER, "999999", ATM_CODE, withdrawAmount)
        ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid PIN");

        Account account = accountRepository.findById(ACCOUNT_NUMBER).orElseThrow();
        assertThat(account.getAvailableBalance())
                .isEqualByComparingTo("6000.00");

        ATM atm = atmRepository.findById(ATM_CODE).orElseThrow();
        assertThat(atm.getCashAvailable())
                .isEqualByComparingTo("3000.00");

        assertThat(transactionRepository.findAll()).hasSize(1);

        Transaction tx = transactionRepository.findAll().get(0);
        assertThat(tx.getTransactionType()).isEqualTo("WITHDRAWAL");
        assertThat(tx.getTransactionStatus()).isEqualTo("FAILED");
        assertThat(tx.getAmount()).isEqualByComparingTo("200.00");

        assertThat(sessionRepository.findAll()).hasSize(1);

        Session session = sessionRepository.findAll().get(0);
        assertThat(session.getSessionStatus()).isEqualTo("ENDED");
        assertThat(session.getEndReason()).isEqualTo("FAILED");
        assertThat(session.getEndTime()).isNotNull();
        assertThat(session.getFailedPinAttempts()).isEqualTo(1);
        assertThat(session.getAuthenticated()).isFalse();
    }

    // FAILURE - INSUFFICIENT ACCOUNT BALANCE
    @Test
    void shouldFailWithdrawal_insufficientBalance() {

        BigDecimal withdrawAmount = new BigDecimal("6100.00");

        assertThatThrownBy(() ->
                withdrawalService.withdraw(CARD_NUMBER, PIN, ATM_CODE, withdrawAmount)
        ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Insufficient balance");

        Account account = accountRepository.findById(ACCOUNT_NUMBER).orElseThrow();
        assertThat(account.getAvailableBalance())
                .isEqualByComparingTo("6000.00");

        ATM atm = atmRepository.findById(ATM_CODE).orElseThrow();
        assertThat(atm.getCashAvailable())
                .isEqualByComparingTo("3000.00");

        assertThat(transactionRepository.findAll()).hasSize(1);

        Transaction tx = transactionRepository.findAll().get(0);
        assertThat(tx.getTransactionType()).isEqualTo("WITHDRAWAL");
        assertThat(tx.getTransactionStatus()).isEqualTo("FAILED");
        assertThat(tx.getAmount()).isEqualByComparingTo("6100.00");

        assertThat(sessionRepository.findAll()).hasSize(1);

        Session session = sessionRepository.findAll().get(0);
        assertThat(session.getSessionStatus()).isEqualTo("ENDED");
        assertThat(session.getEndReason()).isEqualTo("FAILED");
        assertThat(session.getEndTime()).isNotNull();
        assertThat(session.getAuthenticated()).isTrue();
    }

    // FAILURE - ATM INSUFFICIENT CASH
    @Test
    void shouldFailWithdrawal_insufficientCash() {

        BigDecimal withdrawAmount = new BigDecimal("3100.00");

        assertThatThrownBy(() ->
                withdrawalService.withdraw(CARD_NUMBER, PIN, ATM_CODE, withdrawAmount)
        ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("ATM has insufficient cash");

        Account account = accountRepository.findById(ACCOUNT_NUMBER).orElseThrow();
        assertThat(account.getAvailableBalance())
                .isEqualByComparingTo("6000.00");

        ATM atm = atmRepository.findById(ATM_CODE).orElseThrow();
        assertThat(atm.getCashAvailable())
                .isEqualByComparingTo("3000.00");

        assertThat(transactionRepository.findAll()).hasSize(1);

        Transaction tx = transactionRepository.findAll().get(0);
        assertThat(tx.getTransactionType()).isEqualTo("WITHDRAWAL");
        assertThat(tx.getTransactionStatus()).isEqualTo("FAILED");
        assertThat(tx.getAmount()).isEqualByComparingTo("3100.00");

        assertThat(sessionRepository.findAll()).hasSize(1);

        Session session = sessionRepository.findAll().get(0);
        assertThat(session.getSessionStatus()).isEqualTo("ENDED");
        assertThat(session.getEndReason()).isEqualTo("FAILED");
        assertThat(session.getEndTime()).isNotNull();
        assertThat(session.getAuthenticated()).isTrue();
    }

    // FAILURE - ATM TRANSACTION LIMIT EXCEEDED
    @Test
    void shouldFailWithdrawal_transactionLimitExceeded() {

        Account account = accountRepository.findById(ACCOUNT_NUMBER).orElseThrow();
        account.setAvailableBalance(new BigDecimal("7000.00"));
        account.setLedgerBalance(new BigDecimal("7000.00"));
        account.setDailyWithdrawalLimit(new BigDecimal("10000.00"));
        accountRepository.save(account);

        ATM atm = atmRepository.findById(ATM_CODE).orElseThrow();
        atm.setCashAvailable(new BigDecimal("6000.00"));
        atmRepository.save(atm);

        BigDecimal withdrawAmount = new BigDecimal("5100.00");

        assertThatThrownBy(() ->
                withdrawalService.withdraw(CARD_NUMBER, PIN, ATM_CODE, withdrawAmount)
        ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Amount exceeds per transaction limit");

        Account accountAfter = accountRepository.findById(ACCOUNT_NUMBER).orElseThrow();
        assertThat(accountAfter.getAvailableBalance()).isEqualByComparingTo("7000.00");
        assertThat(accountAfter.getLedgerBalance()).isEqualByComparingTo("7000.00");

        ATM atmAfter = atmRepository.findById(ATM_CODE).orElseThrow();
        assertThat(atmAfter.getCashAvailable()).isEqualByComparingTo("6000.00");

        assertThat(transactionRepository.findAll()).hasSize(1);

        Transaction tx = transactionRepository.findAll().get(0);
        assertThat(tx.getTransactionType()).isEqualTo("WITHDRAWAL");
        assertThat(tx.getTransactionStatus()).isEqualTo("FAILED");
        assertThat(tx.getAmount()).isEqualByComparingTo("5100.00");

        assertThat(sessionRepository.findAll()).hasSize(1);

        Session session = sessionRepository.findAll().get(0);
        assertThat(session.getSessionStatus()).isEqualTo("ENDED");
        assertThat(session.getEndReason()).isEqualTo("FAILED");
        assertThat(session.getEndTime()).isNotNull();
        assertThat(session.getAuthenticated()).isTrue();
    }
}