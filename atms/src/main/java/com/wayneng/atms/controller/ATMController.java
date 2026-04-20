package com.wayneng.atms.controller;

import com.wayneng.atms.model.ATM;
import com.wayneng.atms.service.ATMService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/atms")
@RequiredArgsConstructor
public class ATMController {

    private final ATMService atmService;

    @GetMapping("/{atmCode}")
    public ATM getATM(@PathVariable String atmCode) {
        return atmService.getATMByCode(atmCode);
    }

    @PostMapping("/{atmCode}/deduct")
    public String deductCash(
            @PathVariable String atmCode,
            @RequestParam BigDecimal amount
    ) {
        atmService.deductCash(atmCode, amount);
        return "Cash deducted successfully";
    }

    @PostMapping("/{atmCode}/add")
    public String addCash(
            @PathVariable String atmCode,
            @RequestParam BigDecimal amount
    ) {
        atmService.addCash(atmCode, amount);
        return "Cash added successfully";
    }
}