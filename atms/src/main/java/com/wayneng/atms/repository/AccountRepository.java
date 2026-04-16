package com.wayneng.atms.repository;

import com.wayneng.atms.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    Optional<Account> findByCardsCardNumber(String cardNumber);

    List<Account> findByCustomerId(Long customerId);

    List<Account> findByAccountType(String accountType);

    List<Account> findByStatus(String accountStatus);
}