package com.wayneng.atms.integration;

import com.wayneng.atms.model.*;
import com.wayneng.atms.repository.*;
import com.wayneng.atms.service.DepositService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class DepositTest {
    @Autowired private DepositService depositService;
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

    // SUCCESSFUL DEPOSIT
    @Test
    void shouldDepositSuccessfully() {

        BigDecimal depositAmount = new BigDecimal("200.00");

        depositService.deposit(CARD_NUMBER, PIN, ATM_CODE, depositAmount);

        Account updatedAccount = accountRepository.findById(ACCOUNT_NUMBER).orElseThrow();
        assertThat(updatedAccount.getAvailableBalance())
                .isEqualByComparingTo("6200.00");

        ATM updatedATM = atmRepository.findById(ATM_CODE).orElseThrow();
        assertThat(updatedATM.getCashAvailable())
                .isEqualByComparingTo("3200.00");

        Transaction tx = transactionRepository.findAll().get(0);
        assertThat(tx.getTransactionStatus()).isEqualTo("SUCCESS");
        assertThat(tx.getAmount()).isEqualByComparingTo("200.00");

        Session session = sessionRepository.findAll().get(0);
        assertThat(session.getSessionStatus()).isEqualTo("ENDED");
        assertThat(session.getAuthenticated()).isTrue();
    }
}
