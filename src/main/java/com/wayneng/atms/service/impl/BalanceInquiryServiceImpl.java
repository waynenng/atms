package com.wayneng.atms.service.impl;

import com.wayneng.atms.model.Session;
import com.wayneng.atms.service.BalanceInquiryService;
import com.wayneng.atms.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BalanceInquiryServiceImpl implements BalanceInquiryService {

    private final AccountService accountService;

    @Override
    @Transactional(readOnly = true)
    public BigDecimal inquire(Session session) {

        String accountNumber = session.getCard()
                .getAccount()
                .getAccountNumber();

        return accountService.getBalance(accountNumber);
    }
}