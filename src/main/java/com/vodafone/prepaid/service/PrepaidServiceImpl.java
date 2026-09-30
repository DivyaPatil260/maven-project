package com.vodafone.prepaid.service;

import com.vodafone.prepaid.dto.*;
import com.vodafone.prepaid.entity.PrepaidAccount;
import com.vodafone.prepaid.exception.ResourceNotFoundException;
import com.vodafone.prepaid.feign.PostpaidFeignClient;
import com.vodafone.prepaid.repository.PrepaidRepository;
import com.vodafone.prepaid.saga.PrepaidSagaProducer;
import com.vodafone.prepaid.saga.SagaEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PrepaidServiceImpl implements PrepaidService {

    private static final Logger log = LoggerFactory.getLogger(PrepaidServiceImpl.class);

    private final PrepaidRepository prepaidRepository;
    private final PostpaidFeignClient postpaidFeignClient;
    private final RestTemplate restTemplate;
    private final PrepaidSagaProducer sagaProducer;

    @Value("${postpaid.service.url:http://localhost:8082}")
    private String postpaidServiceUrl;

    public PrepaidServiceImpl(PrepaidRepository prepaidRepository,
                              PostpaidFeignClient postpaidFeignClient,
                              RestTemplate restTemplate,
                              PrepaidSagaProducer sagaProducer) {
        this.prepaidRepository = prepaidRepository;
        this.postpaidFeignClient = postpaidFeignClient;
        this.restTemplate = restTemplate;
        this.sagaProducer = sagaProducer;
    }

    @Override
    @Transactional
    public PrepaidAccount createAccount(PrepaidAccount account) {
        log.info("Creating new Prepaid Account for MSISDN: {}", account.getMsisdn());
        return prepaidRepository.save(account);
    }

    @Override
    public PrepaidAccount getAccountByMsisdn(String msisdn) {
        log.info("Fetching Prepaid Account details for MSISDN: {}", msisdn);
        return prepaidRepository.findByMsisdn(msisdn)
                .orElseThrow(() -> new ResourceNotFoundException("Prepaid account not found for MSISDN: " + msisdn));
    }

    @Override
    @Transactional
    public PrepaidAccount recharge(RechargeRequest request) {
        log.info("Processing recharge of amount {} for MSISDN: {}", request.getAmount(), request.getMsisdn());
        PrepaidAccount account = getAccountByMsisdn(request.getMsisdn());
        account.setBalance(account.getBalance().add(request.getAmount()));
        if (request.getPlanName() != null && !request.getPlanName().isEmpty()) {
            account.setPlanName(request.getPlanName());
        }
        return prepaidRepository.save(account);
    }

    @Override
    public PostpaidAccountDto getPostpaidInfoViaFeign(String msisdn) {
        log.info("Invoking Postpaid Service via OpenFeign client for MSISDN: {}", msisdn);
        ApiResponse<PostpaidAccountDto> response = postpaidFeignClient.getPostpaidAccountByMsisdn(msisdn);
        if (response != null && response.isSuccess()) {
            return response.getData();
        }
        throw new ResourceNotFoundException("Could not retrieve postpaid details via Feign for MSISDN: " + msisdn);
    }

    @Override
    public PostpaidBillDto getPostpaidBillViaRestTemplate(String msisdn) {
        log.info("Invoking Postpaid Service via RestTemplate for MSISDN: {}", msisdn);
        String url = postpaidServiceUrl + "/api/postpaid/bill/" + msisdn;
        try {
            ApiResponse response = restTemplate.getForObject(url, ApiResponse.class);
            if (response != null && response.isSuccess()) {
                // Map data from response payload
                return restTemplate.getForObject(url + "/summary", PostpaidBillDto.class);
            }
        } catch (Exception ex) {
            log.error("Error calling Postpaid Service via RestTemplate: {}", ex.getMessage());
        }
        return PostpaidBillDto.builder()
                .msisdn(msisdn)
                .customerName("Synchronous RestTemplate Fallback Customer")
                .currentUnbilled(BigDecimal.ZERO)
                .creditLimit(BigDecimal.valueOf(1000.00))
                .availableCredit(BigDecimal.valueOf(1000.00))
                .build();
    }

    @Override
    @Transactional
    public String initiatePlanMigrationSaga(MigrationRequestDto request) {
        String transactionId = UUID.randomUUID().toString();
        log.info("Initiating SAGA Plan Migration transaction [ID: {}] for MSISDN: {}", transactionId, request.getMsisdn());

        PrepaidAccount account = getAccountByMsisdn(request.getMsisdn());
        
        // Step 1: Mark status as MIGRATING
        account.setStatus("MIGRATING");
        prepaidRepository.save(account);

        // Step 2: Publish MIGRATION_INITIATED event to Kafka
        SagaEvent event = SagaEvent.builder()
                .transactionId(transactionId)
                .msisdn(account.getMsisdn())
                .customerName(account.getCustomerName())
                .targetPlan(request.getTargetPostpaidPlan())
                .creditLimit(request.getInitialCreditLimit() != null ? request.getInitialCreditLimit() : BigDecimal.valueOf(500.00))
                .eventType("MIGRATION_INITIATED")
                .status("PENDING")
                .build();

        sagaProducer.sendMigrationEvent(event);

        return transactionId;
    }
}
