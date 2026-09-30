package com.vodafone.prepaid.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vodafone.prepaid.entity.PrepaidAccount;
import com.vodafone.prepaid.service.PrepaidService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PrepaidController.class)
@AutoConfigureMockMvc
public class PrepaidControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PrepaidService prepaidService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "vodafone_user", roles = {"USER"})
    public void testGetAccount_Authorized_Success() throws Exception {
        PrepaidAccount account = PrepaidAccount.builder()
                .id(1L)
                .msisdn("9876543210")
                .customerName("Jane Doe")
                .balance(BigDecimal.valueOf(250.00))
                .planName("Monthly Max")
                .status("ACTIVE")
                .build();

        when(prepaidService.getAccountByMsisdn("9876543210")).thenReturn(account);

        mockMvc.perform(get("/api/prepaid/account/9876543210")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.msisdn").value("9876543210"))
                .andExpect(jsonPath("$.data.customerName").value("Jane Doe"));
    }

    @Test
    public void testGetAccount_Unauthorized_Returns401() throws Exception {
        mockMvc.perform(get("/api/prepaid/account/9876543210")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
