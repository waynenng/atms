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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // USED
    @Column(unique = true, nullable = false)
    private String atmCode;

    @Column(nullable = false)
    private String locationName;

    // USED
    @Column(nullable = false)
    private String atmStatus;

    // USED
    @Column(nullable = false)
    private BigDecimal cashAvailable;

    @Column(nullable = false)
    private String currency;

    // USED
    @Column(nullable = false)
    private BigDecimal perTransactionLimit;

    @OneToMany(mappedBy = "atm", fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "atm", fetch = FetchType.LAZY)
    private List<Session> sessions;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bank_id", nullable = false)
    private Bank bank;
}
