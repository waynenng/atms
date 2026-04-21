package com.wayneng.atms.unit.controller;

import com.wayneng.atms.controller.ATMController;
import com.wayneng.atms.model.ATM;
import com.wayneng.atms.service.ATMService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ATMController.class)
class ATMControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ATMService atmService;

    @Test
    void testGetATM() throws Exception {

        ATM atm = new ATM();
        atm.setAtmCode("BU01");
        atm.setLocationName("Bandar Utama, PJ");
        atm.setCashAvailable(new BigDecimal("200000"));

        when(atmService.getATMByCode("BU01")).thenReturn(atm);

        mockMvc.perform(get("/api/atms/BU01"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.atmCode").value("BU01"))
                .andExpect(jsonPath("$.locationName").value("Bandar Utama, PJ"))
                .andExpect(jsonPath("$.cashAvailable").value(200000));
    }

    @Test
    void testDeductCash() throws Exception {

        mockMvc.perform(post("/api/atms/BU01/deduct")
                .param("amount", "500"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Cash deducted successfully"));

        verify(atmService).deductCash("BU01", new BigDecimal("500"));
    }

    @Test
    void testAddCash() throws Exception {

        mockMvc.perform(post("/api/atms/BU01/add")
                .param("amount", "500"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Cash added successfully"));

        verify(atmService).addCash("BU01", new BigDecimal("500"));
    }
}