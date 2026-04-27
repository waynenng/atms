package com.wayneng.atms.model;

import java.math.BigDecimal;
import java.util.List;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "atms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ATM {

    @Id
    @Column(unique = true, nullable = false)
    private String atmCode;

    @Column(nullable = false)
    private String locationName;

    @Column(nullable = false)
    private String atmStatus;

    @Column(precision = 19, scale = 2)
    private BigDecimal cashAvailable;

    @Column(nullable = false)
    private String currency;

    @Column(precision = 19, scale = 2)
    private BigDecimal perTransactionLimit;

    @OneToMany(mappedBy = "atm", fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "atm", fetch = FetchType.LAZY)
    private List<Session> sessions;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bank_id", nullable = false)
    private Bank bank;
}
