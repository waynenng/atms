package com.wayneng.atms.service.impl;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import com.wayneng.atms.model.ATM;
import com.wayneng.atms.model.Card;
import com.wayneng.atms.model.Session;
import com.wayneng.atms.repository.SessionRepository;
import com.wayneng.atms.service.ATMService;
import com.wayneng.atms.service.CardService;
import com.wayneng.atms.service.SessionService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final CardService cardService;
    private final ATMService atmService;

    private static final int MAX_FAILED_ATTEMPTS = 3;

    // APPROVED
    @Override
    public Session getSession(String sessionId) {
        return sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
    }

    // APPROVED
    @Override
    public Session startSession(String cardNumber, String atmCode) {

        Card card = cardService.getActiveCard(cardNumber);
        ATM atm = atmService.getATMByCode(atmCode);

        sessionRepository.findByAtm_AtmCodeAndSessionStatus(atmCode, "ACTIVE")
                .ifPresent(s -> {
                    throw new RuntimeException("ATM already in use");
                });

        Session session = new Session();
        session.setCard(card);
        session.setAtm(atm);
        session.setAuthenticated(false);
        session.setSessionStatus("ACTIVE");
        session.setFailedPinAttempts(0);
        session.setStartTime(LocalDateTime.now());

        return sessionRepository.save(session);
    }

    // APPROVED
    @Override
    public Session getActiveSessionByATM(String atmCode) {
        return sessionRepository.findByAtm_AtmCodeAndSessionStatus(atmCode, "ACTIVE")
                .orElseThrow(() -> new RuntimeException("No active session for this ATM"));
    }

    // APPROVED
    @Override
    public void recordFailedPin(String sessionId) {

        Session session = getSession(sessionId);

        int attempts = session.getFailedPinAttempts() + 1;
        session.setFailedPinAttempts(attempts);

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            endSession(sessionId, "CARD_BLOCKED");
            // optionally: cardService.blockCard(...)
        }

        sessionRepository.save(session);
    }

    // APPROVED
    @Override
    public void authenticateSession(String sessionId) {

        Session session = getSession(sessionId);

        session.setAuthenticated(true);

        sessionRepository.save(session);
    }

    // APPROVED
    @Override
    public boolean isAuthenticated(String sessionId) {
        return getSession(sessionId).getAuthenticated();
    }

    // APPROVED
    @Override
    public void endSession(String sessionId, String reason) {

        Session session = getSession(sessionId);

        session.setSessionStatus("ENDED");
        session.setEndTime(LocalDateTime.now());
        session.setEndReason(reason);

        sessionRepository.save(session);
    }
}