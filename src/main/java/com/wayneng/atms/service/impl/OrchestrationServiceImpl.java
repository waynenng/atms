package com.wayneng.atms.service.impl;

import com.wayneng.atms.model.Session;
import com.wayneng.atms.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrchestrationServiceImpl implements OrchestrationService {

    private final SessionService sessionService;
    private final CardService cardService;
    private final WithdrawalService withdrawalService;
    private final DepositService depositService;

    @Override
    public void orchestrate(String operationType,
                        String cardNumber,
                        String pin,
                        String atmCode,
                        BigDecimal amount) {

        boolean success = false;

        Session session = sessionService.startSession(cardNumber, atmCode);

        try {

            boolean validPin = cardService.validatePin(cardNumber, pin);

            if (!validPin) {
                sessionService.recordFailedPin(session.getSessionId());
                throw new RuntimeException("Invalid PIN");
            }

            sessionService.authenticateSession(session.getSessionId());

            if ("WITHDRAWAL".equalsIgnoreCase(operationType)) {

                withdrawalService.withdraw(session, amount);

            } else if ("DEPOSIT".equalsIgnoreCase(operationType)) {

                depositService.deposit(session, amount);

            } else {

                throw new RuntimeException("Unsupported operation");
            }

            success = true;

        } finally {

            sessionService.endSession(
                    session.getSessionId(),
                    success ? "COMPLETED" : "FAILED"
            );
        }
    }
}