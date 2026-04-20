package com.wayneng.atms.service;

import com.wayneng.atms.model.Session;

public interface SessionService {

    Session startSession(String cardNumber, String atmCode);

    Session getActiveSessionByATM(String atmCode);

    void recordFailedPin(Long sessionId);

    void authenticateSession(Long sessionId);

    boolean isAuthenticated(Long sessionId);

    void endSession(Long sessionId, String reason);
}
