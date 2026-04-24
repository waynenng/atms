package com.wayneng.atms.model;

import java.util.List;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "banks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bank {

    @Id
    @Column(unique = true, nullable = false)
    private String bankCode;

    @Column(unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "bank", fetch = FetchType.LAZY)
    private List<Customer> customers;

    @OneToMany(mappedBy = "bank", fetch = FetchType.LAZY)
    private List<ATM> atms;

    @OneToMany(mappedBy = "bank", fetch = FetchType.LAZY)
    private List<Account> accounts;
}
