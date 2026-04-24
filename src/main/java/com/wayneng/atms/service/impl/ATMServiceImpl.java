package com.wayneng.atms.service.impl;

import com.wayneng.atms.model.ATM;
import com.wayneng.atms.repository.ATMRepository;
import com.wayneng.atms.service.ATMService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@Transactional
@RequiredArgsConstructor
public class ATMServiceImpl implements ATMService {

    private final ATMRepository atmRepository;

    public ATM getATMByCode(String atmCode) {
        return atmRepository
            .findByAtmCode(atmCode)
            .orElseThrow(() -> new RuntimeException("ATM not found with code: " + atmCode));
    }

    @Override
    public void deductCash(String atmCode, BigDecimal amount) {
        ATM atm = getATMByCode(atmCode);

        if (!"ACTIVE".equalsIgnoreCase(atm.getAtmStatus())) {
            throw new RuntimeException("ATM is not active");
        }

        if (amount.compareTo(BigDecimal.TEN) < 0) {
            throw new RuntimeException("Invalid withdrawal amount");
        }

        if (amount.compareTo(atm.getPerTransactionLimit()) > 0) {
            throw new RuntimeException("Amount exceeds per transaction limit");
        }

        if (atm.getCashAvailable().compareTo(amount) < 0) {
            throw new RuntimeException("ATM has insufficient cash");
        }

        atm.setCashAvailable(atm.getCashAvailable().subtract(amount));
        atmRepository.save(atm);
    }

    @Override
    public void addCash(String atmCode, BigDecimal amount) {
        ATM atm = getATMByCode(atmCode);

        if (amount.compareTo(BigDecimal.TEN) < 0) {
            throw new RuntimeException("Invalid deposit amount");
        }

        atm.setCashAvailable(atm.getCashAvailable().add(amount));
        atmRepository.save(atm);
    }
}