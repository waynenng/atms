package com.wayneng.atms.dto;

import com.wayneng.atms.model.Session;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class AmountRequest {
    private Session session;
    private BigDecimal amount;
}