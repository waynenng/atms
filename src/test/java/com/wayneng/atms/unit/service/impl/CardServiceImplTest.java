package com.wayneng.atms.unit.service.impl;

import com.wayneng.atms.model.Card;
import com.wayneng.atms.repository.CardRepository;
import com.wayneng.atms.service.impl.CardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private CardServiceImpl cardService;

    private Card card;

    @BeforeEach
    void setUp() {
        card = new Card();
        card.setCardNumber("1111222233334444");
        card.setPinHash("hashedPin");
        card.setCardStatus("ACTIVE");
        card.setFailedPinAttempts(0);
    }

    // getCardByNumber
    @Test
    void shouldReturnCard_whenCardExists() {
        when(cardRepository.findByCardNumber("1111222233334444"))
                .thenReturn(Optional.of(card));

        Card result = cardService.getCardByNumber("1111222233334444");

        assertNotNull(result);
        assertEquals("1111222233334444", result.getCardNumber());
    }

    @Test
    void shouldThrowException_whenCardNotFound() {
        when(cardRepository.findByCardNumber("1111222233334444"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> cardService.getCardByNumber("1111222233334444"));
    }

    // validatePin (SUCCESS)
    @Test
    void shouldValidatePinSuccessfully_andResetAttempts() {
        when(cardRepository.findByCardNumberAndCardStatus("1111222233334444", "ACTIVE"))
                .thenReturn(Optional.of(card));

        when(passwordEncoder.matches("112233", "hashedPin"))
                .thenReturn(true);

        when(cardRepository.findByCardNumber("1111222233334444"))
                .thenReturn(Optional.of(card));

        boolean result = cardService.validatePin("1111222233334444", "112233");

        assertTrue(result);
        assertEquals(0, card.getFailedPinAttempts());

        verify(cardRepository, times(1)).save(card);
    }

    // validatePin (FAIL)
    @Test
    void shouldFailValidation_andIncrementAttempts() {
        when(cardRepository.findByCardNumberAndCardStatus("1111222233334444", "ACTIVE"))
                .thenReturn(Optional.of(card));

        when(passwordEncoder.matches("wrongPin", "hashedPin"))
                .thenReturn(false);

        when(cardRepository.findByCardNumber("1111222233334444"))
                .thenReturn(Optional.of(card));

        boolean result = cardService.validatePin("1111222233334444", "wrongPin");

        assertFalse(result);
        assertEquals(1, card.getFailedPinAttempts());

        verify(cardRepository, times(1)).save(card);
    }

    // incrementFailedAttempts (BLOCK)
    @Test
    void shouldBlockCard_whenMaxAttemptsReached() {
        card.setFailedPinAttempts(4);

        when(cardRepository.findByCardNumber("1111222233334444"))
                .thenReturn(Optional.of(card));

        cardService.incrementFailedAttempts("1111222233334444");

        assertEquals(5, card.getFailedPinAttempts());
        assertEquals("BLOCKED", card.getCardStatus());

        verify(cardRepository).save(card);
    }

    // resetFailedAttempts
    @Test
    void shouldResetFailedAttempts() {
        card.setFailedPinAttempts(3);

        when(cardRepository.findByCardNumber("1111222233334444"))
                .thenReturn(Optional.of(card));

        cardService.resetFailedAttempts("1111222233334444");

        assertEquals(0, card.getFailedPinAttempts());

        verify(cardRepository).save(card);
    }

    // blockCard
    @Test
    void shouldBlockCard() {
        when(cardRepository.findByCardNumber("1111222233334444"))
                .thenReturn(Optional.of(card));

        cardService.blockCard("1111222233334444");

        assertEquals("BLOCKED", card.getCardStatus());

        verify(cardRepository).save(card);
    }
}