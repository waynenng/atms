package com.wayneng.atms.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransactionRequest {
    private String transactionType;
    private BigDecimal amount;
    private String description;
    private String accountNumber;
    private String cardNumber;
    private String sessionId;
    private String atmCode;
}