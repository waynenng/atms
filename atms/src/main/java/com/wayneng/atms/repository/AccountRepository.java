package com.wayneng.atms.repository;

import com.wayneng.atms.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumberAndAccountStatus(String accountNumber, String accountStatus);
}