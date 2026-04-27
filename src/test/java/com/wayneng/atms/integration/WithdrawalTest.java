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
        account.setAvailableBalance(new BigDecimal("1000.00"));
        account.setLedgerBalance(new BigDecimal("1000.00"));
        account.setMinimumBalance(BigDecimal.ZERO);
        account.setDailyWithdrawalLimit(new BigDecimal("5000"));
        account.setCustomer(customer);
        account.setBank(bank);
        accountRepository.save(account);

        Card card = new Card();
        card.setCardNumber(CARD_NUMBER);
        card.setCardType("DEBIT");
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
        atm.setCashAvailable(new BigDecimal("5000"));
        atm.setCurrency("MYR");
        atm.setPerTransactionLimit(new BigDecimal("1000"));
        atm.setBank(bank);
        atmRepository.save(atm);
    }

    // SUCCESSFUL WITHDRAWAL
    @Test
    void shouldWithdrawSuccessfully() {

        BigDecimal withdrawAmount = new BigDecimal("200");

        withdrawalService.withdraw(CARD_NUMBER, PIN, ATM_CODE, withdrawAmount);

        Account updatedAccount = accountRepository.findById(ACCOUNT_NUMBER).orElseThrow();
        assertThat(updatedAccount.getAvailableBalance())
                .isEqualByComparingTo("800.00");

        ATM updatedATM = atmRepository.findById(ATM_CODE).orElseThrow();
        assertThat(updatedATM.getCashAvailable())
                .isEqualByComparingTo("4800");

        Transaction tx = transactionRepository.findAll().get(0);
        assertThat(tx.getTransactionStatus()).isEqualTo("SUCCESS");
        assertThat(tx.getAmount()).isEqualByComparingTo("200");

        Session session = sessionRepository.findAll().get(0);
        assertThat(session.getSessionStatus()).isEqualTo("ENDED");
        assertThat(session.getAuthenticated()).isTrue();
    }

    // FAILURE - INVALID PIN
    @Test
    void shouldFailWithdrawal_invalidPin() {

        BigDecimal withdrawAmount = new BigDecimal("200");

        assertThatThrownBy(() ->
                withdrawalService.withdraw(CARD_NUMBER, "9999", ATM_CODE, withdrawAmount)
        ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid PIN");

        Account account = accountRepository.findById(ACCOUNT_NUMBER).orElseThrow();
        assertThat(account.getAvailableBalance())
                .isEqualByComparingTo("1000.00");

        ATM atm = atmRepository.findById(ATM_CODE).orElseThrow();
        assertThat(atm.getCashAvailable())
                .isEqualByComparingTo("5000");

        assertThat(transactionRepository.findAll()).hasSize(1);

        Transaction tx = transactionRepository.findAll().get(0);
        assertThat(tx.getTransactionType()).isEqualTo("WITHDRAWAL");
        assertThat(tx.getTransactionStatus()).isEqualTo("FAILED");
        assertThat(tx.getAmount()).isEqualByComparingTo("200");

        assertThat(sessionRepository.findAll()).hasSize(1);

        Session session = sessionRepository.findAll().get(0);
        assertThat(session.getSessionStatus()).isEqualTo("ENDED");
        assertThat(session.getEndReason()).isEqualTo("FAILED");
        assertThat(session.getEndTime()).isNotNull();

        assertThat(session.getFailedPinAttempts()).isEqualTo(1);

        assertThat(session.getAuthenticated()).isFalse();
    }
}