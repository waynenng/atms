package com.wayneng.atms.service;

import java.math.BigDecimal;

public interface BalanceInquiryService {

    BigDecimal getAvailableBalance(String cardNumber);
}