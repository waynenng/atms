package com.wayneng.atms.model;

import java.util.List;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @OneToMany(mappedBy = "card")
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "card")
    private List<Session> sessions;

    @ManyToOne
    private Customer customer;

    @ManyToOne
    private Account account;
}
