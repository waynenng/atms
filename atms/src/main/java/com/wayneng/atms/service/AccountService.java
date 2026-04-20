package com.wayneng.atms.service;

import java.math.BigDecimal;
import com.wayneng.atms.model.Account;

public interface AccountService {

    Account getActiveAccount(String accountNumber);

    BigDecimal getBalance(String accountNumber);

    void deposit(String accountNumber, BigDecimal amount);

    void withdraw(String accountNumber, BigDecimal amount);
}