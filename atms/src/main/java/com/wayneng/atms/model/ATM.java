package com.wayneng.atms.model;

import java.util.List;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ATM {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    

    @OneToMany(mappedBy = "atm")
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "atm")
    private List<Session> sessions;

    @ManyToOne
    private Bank bank;
}
