package com.wayneng.atms.service;

import java.math.BigDecimal;

public interface WithdrawalService {

    void withdraw(String cardNumber, String pin, String atmCode, BigDecimal amount);
}
