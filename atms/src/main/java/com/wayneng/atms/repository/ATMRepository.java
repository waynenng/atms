package com.wayneng.atms.repository;

import com.wayneng.atms.model.ATM;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface ATMRepository extends JpaRepository<ATM, Long> {

    Optional<ATM> findByAtmCode(String atmCode);

    List<ATM> findByAtmStatus(String atmStatus);

    List<ATM> findByLocationName(String locationName);
}