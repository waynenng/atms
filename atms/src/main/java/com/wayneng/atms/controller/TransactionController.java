package com.wayneng.atms.controller;

import com.wayneng.atms.dto.TransactionRequest;
import com.wayneng.atms.model.Transaction;
import com.wayneng.atms.service.TransactionService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public Transaction createTransaction(@RequestBody TransactionRequest request) {
        return transactionService.createTransaction(
                request.getTransactionType(),
                request.getAmount(),
                request.getAccountNumber(),
                request.getCardNumber(),
                request.getSessionId(),
                request.getAtmCode()
        );
    }

    @PutMapping("/{transactionId}/status")
    public void updateTransactionStatus(
            @PathVariable String transactionId,
            @RequestParam String status) {

        transactionService.updateTransactionStatus(transactionId, status);
    }

    @GetMapping("/{transactionId}")
    public Transaction getTransaction(@PathVariable String transactionId) {
        return transactionService.getTransactionById(transactionId);
    }
}