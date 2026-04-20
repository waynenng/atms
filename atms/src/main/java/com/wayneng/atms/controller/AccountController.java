package com.wayneng.atms.controller;

import com.wayneng.atms.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{accountNumber}/balance")
    public BigDecimal getBalance(@PathVariable String accountNumber) {
        return accountService.getBalance(accountNumber);
    }

    @PostMapping("/{accountNumber}/deposit")
    public String deposit(
            @PathVariable String accountNumber,
            @RequestParam BigDecimal amount
    ) {
        accountService.deposit(accountNumber, amount);
        return "Deposit successful";
    }

    @PostMapping("/{accountNumber}/withdraw")
    public String withdraw(
            @PathVariable String accountNumber,
            @RequestParam BigDecimal amount
    ) {
        accountService.withdraw(accountNumber, amount);
        return "Withdrawal successful";
    }
}