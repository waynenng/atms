package com.wayneng.atms.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    
    @Id
    @GeneratedValue
    private Long id;

    private BigDecimal amount;

    private LocalDateTime timestamp;

    @ManyToOne
    private Account account;
}
