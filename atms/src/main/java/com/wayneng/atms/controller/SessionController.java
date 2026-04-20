package com.wayneng.atms.controller;

import com.wayneng.atms.model.Session;
import com.wayneng.atms.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @PostMapping("/start")
    public Session startSession(
            @RequestParam String cardNumber,
            @RequestParam String atmCode
    ) {
        return sessionService.startSession(cardNumber, atmCode);
    }

    @GetMapping("/{sessionId}")
    public Session getSession(@PathVariable String sessionId) {
        return sessionService.getSession(sessionId);
    }

    @GetMapping("/atm/{atmCode}")
    public Session getActiveSession(@PathVariable String atmCode) {
        return sessionService.getActiveSessionByATM(atmCode);
    }

    @PostMapping("/{sessionId}/authenticate")
    public void authenticateSession(@PathVariable String sessionId) {
        sessionService.authenticateSession(sessionId);
    }

    @PostMapping("/{sessionId}/fail-pin")
    public void recordFailedPin(@PathVariable String sessionId) {
        sessionService.recordFailedPin(sessionId);
    }

    @GetMapping("/{sessionId}/authenticated")
    public boolean isAuthenticated(@PathVariable String sessionId) {
        return sessionService.isAuthenticated(sessionId);
    }

    @PostMapping("/{sessionId}/end")
    public void endSession(
            @PathVariable String sessionId,
            @RequestParam String reason
    ) {
        sessionService.endSession(sessionId, reason);
    }
}