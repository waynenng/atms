package com.wayneng.atms.service;

import java.math.BigDecimal;

public interface OrchestrationService {

    void orchestrate(String operationType, String cardNumber, String pin, String atmCode, BigDecimal amount);
}
