package com.wayneng.atms.model;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // USED
    @Column(unique = true, nullable = false)
    private String sessionId;

    // USED
    @Column(nullable = false)
    private Boolean authenticated;

    // USED
    @Column(nullable = false)
    private String sessionStatus;
    
    // USED
    @Column(nullable = false)
    private Integer failedPinAttempts;

    // USED
    @Column(nullable = false)
    private LocalDateTime startTime;

    // USED
    private LocalDateTime endTime;

    // USED
    private String endReason;
    
    @OneToMany(mappedBy = "session", fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "atm_id", nullable = false)
    private ATM atm;
}
