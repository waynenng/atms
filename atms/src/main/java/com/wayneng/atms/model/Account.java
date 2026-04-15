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

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String accountType;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private String status;

    @Column(precision = 19, scale = 2)
    private BigDecimal availableBalance = BigDecimal.ZERO;

    @Column(precision = 19, scale = 2)
    private BigDecimal ledgerBalance = BigDecimal.ZERO;

    @Column(precision = 19, scale = 2)
    private BigDecimal minimumBalance;

    @Column(precision = 19, scale = 2)
    private BigDecimal dailyWithdrawalLimit;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<Card> cards;

    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    private Bank bank;
}