package com.wayneng.atms.service;

import java.math.BigDecimal;

public interface AccountService {

    // USED
    BigDecimal getBalance(String accountNumber);

    // USED
    void deposit(String accountNumber, BigDecimal amount);

    // USED
    void withdraw(String accountNumber, BigDecimal amount);
}