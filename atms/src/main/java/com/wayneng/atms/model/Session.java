package com.wayneng.atms.model;

import java.util.List;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    
    @OneToMany(mappedBy = "session")
    private List<Transaction> transactions;

    @ManyToOne
    private Card card;

    @ManyToOne
    private ATM atm;
}
