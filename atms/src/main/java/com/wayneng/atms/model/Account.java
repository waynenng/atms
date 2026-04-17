package com.wayneng.atms.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // USED
    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String accountType;

    @Column(nullable = false)
    private String currency;

    // USED
    @Column(nullable = false)
    private String accountStatus;

    // USED
    @Column(precision = 19, scale = 2)
    private BigDecimal availableBalance = BigDecimal.ZERO;

    // USED
    @Column(precision = 19, scale = 2)
    private BigDecimal ledgerBalance = BigDecimal.ZERO;

    // USED
    @Column(precision = 19, scale = 2)
    private BigDecimal minimumBalance;

    // USED
    @Column(precision = 19, scale = 2)
    private BigDecimal dailyWithdrawalLimit;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<Card> cards;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bank_id", nullable = false)
    private Bank bank;
}