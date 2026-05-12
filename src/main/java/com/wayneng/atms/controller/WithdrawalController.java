package com.wayneng.atms.controller;

import com.wayneng.atms.dto.AmountRequest;
import com.wayneng.atms.service.WithdrawalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/withdrawal")
@RequiredArgsConstructor
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    @PostMapping
    public ResponseEntity<String> withdraw(
            @RequestBody AmountRequest request
    ) {
        try {
            withdrawalService.withdraw(request.getSession(), request.getAmount());
            return ResponseEntity.ok("Withdrawal successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Withdrawal failed: " + e.getMessage());
        }
    }
}