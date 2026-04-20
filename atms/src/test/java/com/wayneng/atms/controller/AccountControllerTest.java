package com.wayneng.atms.controller;

import com.wayneng.atms.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @Test
    void getBalance_shouldReturnBalance() throws Exception {

        String accountNumber = "1212121212";
        BigDecimal balance = BigDecimal.valueOf(5000);

        when(accountService.getBalance(accountNumber)).thenReturn(balance);

        mockMvc.perform(get("/api/accounts/{accountNumber}/balance", accountNumber))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string("5000"));
    }

    @Test
    void deposit_shouldReturnSuccessMessage() throws Exception {

        String accountNumber = "1212121212";
        BigDecimal amount = BigDecimal.valueOf(5000);

        mockMvc.perform(post("/api/accounts/{accountNumber}/deposit", accountNumber)
                    .param("amount", amount.toString())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string("Deposit successful"));

        verify(accountService).deposit(accountNumber, amount);
    }

    @Test
    void withdraw_shouldReturnSuccessMessage() throws Exception {

        String accountNumber = "1212121212";
        BigDecimal amount = BigDecimal.valueOf(5000);

        mockMvc.perform(post("/api/accounts/{accountNumber}/withdraw", accountNumber)
                    .param("amount", amount.toString())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string("Withdrawal successful"));

        verify(accountService).withdraw(accountNumber, amount);
    }
}