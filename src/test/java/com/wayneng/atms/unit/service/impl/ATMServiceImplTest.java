package com.wayneng.atms.unit.service.impl;

import com.wayneng.atms.model.ATM;
import com.wayneng.atms.repository.ATMRepository;
import com.wayneng.atms.service.impl.ATMServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ATMServiceImplTest {

    @Mock
    private ATMRepository atmRepository;

    @InjectMocks
    private ATMServiceImpl atmService;

    private ATM atm;

    @BeforeEach
    void setUp() {
        atm = new ATM();
        atm.setAtmCode("ATM001");
        atm.setAtmStatus("ACTIVE");
        atm.setCashAvailable(new BigDecimal("1000"));
        atm.setPerTransactionLimit(new BigDecimal("500"));
    }

    // getATMByCode
    @Test
    void shouldReturnATM_whenATMExists() {
        when(atmRepository.findByAtmCode("ATM001"))
                .thenReturn(Optional.of(atm));

        ATM result = atmService.getATMByCode("ATM001");

        assertNotNull(result);
        assertEquals("ATM001", result.getAtmCode());
    }

    @Test
    void shouldThrowException_whenATMNotFound() {
        when(atmRepository.findByAtmCode("ATM001"))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> atmService.getATMByCode("ATM001"));

        assertEquals("ATM not found with code: ATM001", ex.getMessage());
    }

    // deductCash
    @Test
    void shouldDeductCashSuccessfully() {
        when(atmRepository.findByAtmCode("ATM001"))
                .thenReturn(Optional.of(atm));

        atmService.deductCash("ATM001", new BigDecimal("200"));

        assertEquals(new BigDecimal("800"), atm.getCashAvailable());
        verify(atmRepository).save(atm);
    }

    @Test
    void shouldThrowException_whenATMNotActive() {
        atm.setAtmStatus("INACTIVE");

        when(atmRepository.findByAtmCode("ATM001"))
                .thenReturn(Optional.of(atm));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> atmService.deductCash("ATM001", new BigDecimal("100")));

        assertEquals("ATM is not active", ex.getMessage());
    }

    @Test
    void shouldThrowException_whenAmountBelowMinimum() {
        when(atmRepository.findByAtmCode("ATM001"))
                .thenReturn(Optional.of(atm));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> atmService.deductCash("ATM001", new BigDecimal("5")));

        assertEquals("Invalid withdrawal amount", ex.getMessage());
    }

    @Test
    void shouldThrowException_whenExceedsTransactionLimit() {
        when(atmRepository.findByAtmCode("ATM001"))
                .thenReturn(Optional.of(atm));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> atmService.deductCash("ATM001", new BigDecimal("600")));

        assertEquals("Amount exceeds per transaction limit", ex.getMessage());
    }

    @Test
    void shouldThrowException_whenInsufficientATMFunds() {
        atm.setCashAvailable(new BigDecimal("100"));

        when(atmRepository.findByAtmCode("ATM001"))
                .thenReturn(Optional.of(atm));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> atmService.deductCash("ATM001", new BigDecimal("200")));

        assertEquals("ATM has insufficient cash", ex.getMessage());
    }

    // addCash
    @Test
    void shouldAddCashSuccessfully() {
        when(atmRepository.findByAtmCode("ATM001"))
                .thenReturn(Optional.of(atm));

        atmService.addCash("ATM001", new BigDecimal("200"));

        assertEquals(new BigDecimal("1200"), atm.getCashAvailable());
        verify(atmRepository).save(atm);
    }

    @Test
    void shouldThrowException_whenDepositBelowMinimum() {
        when(atmRepository.findByAtmCode("ATM001"))
                .thenReturn(Optional.of(atm));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> atmService.addCash("ATM001", new BigDecimal("5")));

        assertEquals("Invalid deposit amount", ex.getMessage());
    }
}