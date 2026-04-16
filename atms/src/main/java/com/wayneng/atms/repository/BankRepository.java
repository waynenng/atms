package com.wayneng.atms.repository;

import com.wayneng.atms.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BankRepository extends JpaRepository<Bank, Long> {

    Optional<Bank> findByName(String name);

    Optional<Bank> findByBankCode(String bankCode);
}