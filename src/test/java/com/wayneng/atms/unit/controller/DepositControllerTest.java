package com.wayneng.atms.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wayneng.atms.controller.DepositController;
import com.wayneng.atms.dto.AtmRequest;
import com.wayneng.atms.service.DepositService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.math.BigDecimal;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DepositControllerTest {

    @Mock
    private DepositService depositService;

    @InjectMocks
    private DepositController depositController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(depositController)
                .build();

        objectMapper = new ObjectMapper();
    }

    // SUCCESS CASE
    @Test
    void shouldReturn200_whenDepositSuccessful() throws Exception {

        AtmRequest request = new AtmRequest();
        request.setCardNumber("1234567890123456");
        request.setPin("1234");
        request.setAtmCode("ATM001");
        request.setAmount(BigDecimal.valueOf(100));

        doNothing().when(depositService).deposit(
                request.getCardNumber(),
                request.getPin(),
                request.getAtmCode(),
                request.getAmount()
        );

        mockMvc.perform(post("/api/deposits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Deposit successful"));
    }

    // BUSINESS ERROR (400)
    @Test
    void shouldReturn400_whenInvalidPin() throws Exception {

        AtmRequest request = new AtmRequest();
        request.setCardNumber("1234567890123456");
        request.setPin("9999");
        request.setAtmCode("ATM001");
        request.setAmount(BigDecimal.valueOf(100));

        doThrow(new RuntimeException("Invalid PIN"))
                .when(depositService)
                .deposit(
                        request.getCardNumber(),
                        request.getPin(),
                        request.getAtmCode(),
                        request.getAmount()
                );

        mockMvc.perform(post("/api/deposits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid PIN"));
    }
}
