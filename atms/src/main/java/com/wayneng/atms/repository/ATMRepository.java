package com.wayneng.atms.repository;

import com.wayneng.atms.model.ATM;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ATMRepository extends JpaRepository<ATM, Long> {

    Optional<ATM> findByAtmCode(String atmCode);
}