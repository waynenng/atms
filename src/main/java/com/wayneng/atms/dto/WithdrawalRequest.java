package com.wayneng.atms.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class WithdrawalRequest {

    private String cardNumber;
    private String pin;
    private String atmCode;
    private BigDecimal amount;
}