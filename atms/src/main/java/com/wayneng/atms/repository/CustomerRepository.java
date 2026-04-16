package com.wayneng.atms.repository;

import com.wayneng.atms.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByFullName(String fullName);

    Optional<Customer> findByAccountsAccountNumber(String accountNumber);

    Optional<Customer> findByCustomerNumber(String customerNumber);

    List<Customer> findByStatus(String status);

    boolean existsByFullName(String fullName);
}