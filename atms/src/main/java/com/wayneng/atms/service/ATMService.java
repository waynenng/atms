package com.wayneng.atms.service;

import java.math.BigDecimal;

import com.wayneng.atms.model.ATM;

public interface ATMService {

    ATM getATMByCode(String atmCode);

    void deductCash(String atmCode, BigDecimal amount);

    void addCash(String atmCode, BigDecimal amount);
}