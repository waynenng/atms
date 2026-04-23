package com.wayneng.atms.unit.service.impl;

import com.wayneng.atms.model.*;
import com.wayneng.atms.repository.TransactionRepository;
import com.wayneng.atms.service.ATMService;
import com.wayneng.atms.service.AccountService;
import com.wayneng.atms.service.CardService;
import com.wayneng.atms.service.SessionService;
import com.wayneng.atms.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private CardService cardService;

    @Mock
    private SessionService sessionService;

    @Mock
    private ATMService atmService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    // createTransaction()
    @Test
    void testCreateTransaction_success() {

        String transactionType = "WITHDRAWAL";
        BigDecimal amount = BigDecimal.valueOf(100);
        String description = "ATM Withdrawal";
        String accountNumber = "ACC123";
        String cardNumber = "CARD123";
        String sessionId = "SESSION123";
        String atmCode = "ATM001";

        Account account = new Account();
        Card card = new Card();
        Session session = new Session();
        ATM atm = new ATM();

        when(accountService.getActiveAccount(accountNumber)).thenReturn(account);
        when(cardService.getCardByNumber(cardNumber)).thenReturn(card);
        when(sessionService.getSession(sessionId)).thenReturn(session);
        when(atmService.getATMByCode(atmCode)).thenReturn(atm);

        Transaction savedTransaction = new Transaction();
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);

        Transaction result = transactionService.createTransaction(
                transactionType,
                amount,
                description,
                accountNumber,
                cardNumber,
                sessionId,
                atmCode
        );

        verify(transactionRepository, times(1)).save(transactionCaptor.capture());

        Transaction captured = transactionCaptor.getValue();

        assertEquals(transactionType, captured.getTransactionType());
        assertEquals("PENDING", captured.getTransactionStatus());
        assertEquals(amount, captured.getAmount());
        assertEquals(description, captured.getDescription());
        assertEquals(account, captured.getAccount());
        assertEquals(card, captured.getCard());
        assertEquals(session, captured.getSession());
        assertEquals(atm, captured.getAtm());

        assertNotNull(captured.getTransactionTime());

        assertEquals(savedTransaction, result);
    }

    // updateTransactionStatus()
    @Test
    void testUpdateTransactionStatus_success() {

        String transactionId = "TXN123";
        String newStatus = "SUCCESS";

        Transaction transaction = new Transaction();
        transaction.setTransactionId(transactionId);
        transaction.setTransactionStatus("PENDING");

        when(transactionRepository.findByTransactionId(transactionId))
                .thenReturn(Optional.of(transaction));

        transactionService.updateTransactionStatus(transactionId, newStatus);

        assertEquals(newStatus, transaction.getTransactionStatus());
        verify(transactionRepository, times(1)).save(transaction);
    }

    // getTransactionById() - success
    @Test
    void testGetTransactionById_success() {

        String transactionId = "TXN123";

        Transaction transaction = new Transaction();

        when(transactionRepository.findByTransactionId(transactionId))
                .thenReturn(Optional.of(transaction));

        Transaction result = transactionService.getTransactionById(transactionId);

        assertEquals(transaction, result);
    }

    // getTransactionById() - not found
    @Test
    void testGetTransactionById_notFound() {

        String transactionId = "TXN999";

        when(transactionRepository.findByTransactionId(transactionId))
                .thenReturn(Optional.empty());


        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                transactionService.getTransactionById(transactionId)
        );

        assertEquals("Transaction not found", exception.getMessage());
    }
}