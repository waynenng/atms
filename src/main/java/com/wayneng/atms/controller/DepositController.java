package com.wayneng.atms.controller;

import com.wayneng.atms.dto.AtmRequest;
import com.wayneng.atms.service.DepositService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deposits")
@RequiredArgsConstructor
public class DepositController {

    private final DepositService depositService;

    @PostMapping
    public ResponseEntity<String> withdraw(@RequestBody AtmRequest request) {

        try {
            depositService.deposit(
                    request.getCardNumber(),
                    request.getPin(),
                    request.getAtmCode(),
                    request.getAmount()
            );

            return ResponseEntity.ok("Deposit successful");

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
