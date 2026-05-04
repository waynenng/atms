package com.wayneng.atms.controller;

import com.wayneng.atms.dto.AtmRequest;
import com.wayneng.atms.service.WithdrawalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/withdrawals")
@RequiredArgsConstructor
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    @PostMapping
    public ResponseEntity<String> withdraw(@RequestBody AtmRequest request) {

        try {
            withdrawalService.withdraw(
                    request.getCardNumber(),
                    request.getPin(),
                    request.getAtmCode(),
                    request.getAmount()
            );

            return ResponseEntity.ok("Withdrawal successful");

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong");
        }
    }
}