package com.wayneng.atms.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
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

    private String accountType;
    private String currency;
    private String status;
    private BigDecimal availableBalance;
    private BigDecimal ledgerBalance;
    private BigDecimal minimumBalance;
    private BigDecimal dailyWithdrawalLimit;

    @OneToMany(mappedBy = "account")
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "account")
    private List<Card> cards;

    @ManyToOne
    private Customer customer;
}