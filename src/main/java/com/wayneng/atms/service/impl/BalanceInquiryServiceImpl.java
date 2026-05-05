package com.wayneng.atms.service.impl;

import com.wayneng.atms.model.Account;
import com.wayneng.atms.repository.AccountRepository;
import com.wayneng.atms.service.BalanceInquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BalanceInquiryServiceImpl implements BalanceInquiryService {

    private final AccountRepository accountRepository;

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getAvailableBalance(String cardNumber) {

        Account account = accountRepository.findByCards_CardNumber(cardNumber)
                .orElseThrow(() -> new RuntimeException("Account not found for card: " + cardNumber));

        return account.getAvailableBalance();
    }
}