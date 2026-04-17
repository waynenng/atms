package com.wayneng.atms.service;

import java.math.BigDecimal;

public interface ATMService {

    void deductCash(String atmCode, BigDecimal amount);

    void addCash(String atmCode, BigDecimal amount);
}