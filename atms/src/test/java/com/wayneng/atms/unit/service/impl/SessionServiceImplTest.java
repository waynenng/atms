package com.wayneng.atms.unit.service.impl;

import com.wayneng.atms.model.ATM;
import com.wayneng.atms.model.Card;
import com.wayneng.atms.model.Session;
import com.wayneng.atms.repository.SessionRepository;
import com.wayneng.atms.service.ATMService;
import com.wayneng.atms.service.CardService;
import com.wayneng.atms.service.impl.SessionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceImplTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private CardService cardService;

    @Mock
    private ATMService atmService;

    @InjectMocks
    private SessionServiceImpl sessionService;

    private Session session;
    private Card card;
    private ATM atm;

    @BeforeEach
    void setUp() {
        card = new Card();
        atm = new ATM();
        session = new Session();
        session.setSessionId("S1");
        session.setAuthenticated(false);
        session.setFailedPinAttempts(0);
    }

    // getSession
    @Test
    void getSession_success() {
        when(sessionRepository.findBySessionId("S1"))
                .thenReturn(Optional.of(session));

        Session result = sessionService.getSession("S1");

        assertNotNull(result);
        assertEquals("S1", result.getSessionId());
    }

    @Test
    void getSession_notFound() {
        when(sessionRepository.findBySessionId("S1"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> sessionService.getSession("S1"));
    }

    // startSession
    @Test
    void startSession_success() {
        when(cardService.getActiveCard("123"))
                .thenReturn(card);

        when(atmService.getATMByCode("ATM1"))
                .thenReturn(atm);

        when(sessionRepository.findByAtm_AtmCodeAndSessionStatus("ATM1", "ACTIVE"))
                .thenReturn(Optional.empty());

        when(sessionRepository.save(any(Session.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Session result = sessionService.startSession("123", "ATM1");

        assertNotNull(result);
        assertEquals("ACTIVE", result.getSessionStatus());
        assertFalse(result.getAuthenticated());
        assertEquals(0, result.getFailedPinAttempts());

        verify(sessionRepository, times(1)).save(any(Session.class));
    }

    @Test
    void startSession_atmAlreadyInUse() {
        when(cardService.getActiveCard("123"))
                .thenReturn(card);

        when(atmService.getATMByCode("ATM1"))
                .thenReturn(atm);

        when(sessionRepository.findByAtm_AtmCodeAndSessionStatus("ATM1", "ACTIVE"))
                .thenReturn(Optional.of(new Session()));

        assertThrows(RuntimeException.class,
                () -> sessionService.startSession("123", "ATM1"));

        verify(sessionRepository, never()).save(any());
    }

    // getActiveSessionByATM
    @Test
    void getActiveSessionByATM_success() {
        when(sessionRepository.findByAtm_AtmCodeAndSessionStatus("ATM1", "ACTIVE"))
                .thenReturn(Optional.of(session));

        Session result = sessionService.getActiveSessionByATM("ATM1");

        assertNotNull(result);
    }

    @Test
    void getActiveSessionByATM_notFound() {
        when(sessionRepository.findByAtm_AtmCodeAndSessionStatus("ATM1", "ACTIVE"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> sessionService.getActiveSessionByATM("ATM1"));
    }

    // recordFailedPin
    @Test
    void recordFailedPin_lessThanMaxAttempts() {
        session.setFailedPinAttempts(1);

        when(sessionRepository.findBySessionId("S1"))
                .thenReturn(Optional.of(session));

        sessionService.recordFailedPin("S1");

        assertEquals(2, session.getFailedPinAttempts());
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    void recordFailedPin_reachesMaxAttempts() {
        session.setFailedPinAttempts(2);

        when(sessionRepository.findBySessionId("S1"))
                .thenReturn(Optional.of(session));

        sessionService.recordFailedPin("S1");

        assertEquals(3, session.getFailedPinAttempts());

        verify(sessionRepository, atLeastOnce()).save(session);
    }

    // authenticateSession
    @Test
    void authenticateSession_success() {
        when(sessionRepository.findBySessionId("S1"))
                .thenReturn(Optional.of(session));

        sessionService.authenticateSession("S1");

        assertTrue(session.getAuthenticated());
        verify(sessionRepository).save(session);
    }

    // isAuthenticated
    @Test
    void isAuthenticated_true() {
        session.setAuthenticated(true);

        when(sessionRepository.findBySessionId("S1"))
                .thenReturn(Optional.of(session));

        boolean result = sessionService.isAuthenticated("S1");

        assertTrue(result);
    }

    @Test
    void isAuthenticated_false() {
        session.setAuthenticated(false);

        when(sessionRepository.findBySessionId("S1"))
                .thenReturn(Optional.of(session));

        boolean result = sessionService.isAuthenticated("S1");

        assertFalse(result);
    }

    // endSession
    @Test
    void endSession_success() {
        when(sessionRepository.findBySessionId("S1"))
                .thenReturn(Optional.of(session));

        sessionService.endSession("S1", "TIMEOUT");

        assertEquals("ENDED", session.getSessionStatus());
        assertEquals("TIMEOUT", session.getEndReason());
        assertNotNull(session.getEndTime());

        verify(sessionRepository).save(session);
    }
}