package com.vodafone.prepaid.service;

import com.vodafone.prepaid.dto.RechargeRequest;
import com.vodafone.prepaid.entity.PrepaidAccount;
import com.vodafone.prepaid.exception.ResourceNotFoundException;
import com.vodafone.prepaid.feign.PostpaidFeignClient;
import com.vodafone.prepaid.repository.PrepaidRepository;
import com.vodafone.prepaid.saga.PrepaidSagaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PrepaidServiceTest {

    @Mock
    private PrepaidRepository prepaidRepository;

    @Mock
    private PostpaidFeignClient postpaidFeignClient;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private PrepaidSagaProducer sagaProducer;

    @InjectMocks
    private PrepaidServiceImpl prepaidService;

    private PrepaidAccount sampleAccount;

    @BeforeEach
    public void setUp() {
        sampleAccount = PrepaidAccount.builder()
                .id(1L)
                .msisdn("9876543210")
                .customerName("John Doe")
                .balance(BigDecimal.valueOf(100.00))
                .planName("Super Recharge 299")
                .status("ACTIVE")
                .build();
    }

    @Test
    public void testGetAccountByMsisdn_Success() {
        when(prepaidRepository.findByMsisdn("9876543210")).thenReturn(Optional.of(sampleAccount));

        PrepaidAccount found = prepaidService.getAccountByMsisdn("9876543210");

        assertNotNull(found);
        assertEquals("9876543210", found.getMsisdn());
        assertEquals("John Doe", found.getCustomerName());
        verify(prepaidRepository, times(1)).findByMsisdn("9876543210");
    }

    @Test
    public void testGetAccountByMsisdn_NotFound() {
        when(prepaidRepository.findByMsisdn("0000000000")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            prepaidService.getAccountByMsisdn("0000000000");
        });
    }

    @Test
    public void testRecharge_Success() {
        when(prepaidRepository.findByMsisdn("9876543210")).thenReturn(Optional.of(sampleAccount));
        when(prepaidRepository.save(any(PrepaidAccount.class))).thenAnswer(i -> i.getArguments()[0]);

        RechargeRequest req = new RechargeRequest("9876543210", BigDecimal.valueOf(50.00), "Unlimited 399");
        PrepaidAccount updated = prepaidService.recharge(req);

        assertNotNull(updated);
        assertEquals(BigDecimal.valueOf(150.00), updated.getBalance());
        assertEquals("Unlimited 399", updated.getPlanName());
        verify(prepaidRepository, times(1)).save(any(PrepaidAccount.class));
    }
}
