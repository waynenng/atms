package com.wayneng.atms.model;

import java.time.LocalDate;
import java.util.List;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // USED
    @Column(unique = true, nullable = false)
    private String cardNumber;

    @Column(nullable = false)
    private String cardType;

    // USED
    @Column(nullable = false)
    private String cardStatus;

    // USED
    @Column(nullable = false)
    private String pinHash;

    @Column(nullable = false)
    private LocalDate expiryDate;

    // USED
    @Column(nullable = false)
    private int failedPinAttempts;

    @OneToMany(mappedBy = "card", fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "card", fetch = FetchType.LAZY)
    private List<Session> sessions;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
}
