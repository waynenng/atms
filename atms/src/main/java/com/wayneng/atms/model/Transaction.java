package com.wayneng.atms.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    

    @ManyToOne
    private Account account;

    @ManyToOne
    private Card card;

    @ManyToOne
    private Session session;

    @ManyToOne
    private ATM atm;
}
