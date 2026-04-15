package com.wayneng.atms.model;

import java.util.List;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    

    @OneToMany(mappedBy = "bank")
    private List<Customer> cusomters;

    @OneToMany(mappedBy = "bank")
    private List<ATM> atms;

    @OneToMany(mappedBy = "bank")
    private List<ATM> accounts;
}
