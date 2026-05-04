package com.wayneng.atms.service;

import java.math.BigDecimal;

public interface DepositService {

    void deposit(String cardNumber, String pin, String atmCode, BigDecimal amount);
}
