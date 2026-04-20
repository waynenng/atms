package com.wayneng.atms.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // USED
    @Column(unique = true, nullable = false)
    private String transactionId;

    // USED
    @Column(nullable = false)
    private String transactionType;

    // USED
    @Column(nullable = false)
    private String transactionStatus;

    // USED
    @Column(nullable = false)
    private LocalDateTime transactionTime;

    // USED
    @Column(nullable = true)
    private BigDecimal amount;

    // USED
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "atm_id", nullable = false)
    private ATM atm;
}