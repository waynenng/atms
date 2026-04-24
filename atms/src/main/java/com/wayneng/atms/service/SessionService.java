package com.wayneng.atms.service;

import com.wayneng.atms.model.Session;

public interface SessionService {

    Session getSession(String sessionId);

    Session startSession(String cardNumber, String atmCode);

    Session getActiveSessionByATM(String atmCode);

    void recordFailedPin(String sessionId);

    void authenticateSession(String sessionId);

    boolean isAuthenticated(String sessionId);

    void endSession(String sessionId, String reason);
}
