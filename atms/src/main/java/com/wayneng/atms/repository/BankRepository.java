package com.wayneng.atms.repository;

import com.wayneng.atms.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankRepository extends JpaRepository<Bank, String> {


}