package com.wayneng.atms.service;

import com.wayneng.atms.model.Session;
import java.math.BigDecimal;

public interface DepositService {

    void deposit(Session session, BigDecimal amount);
}
