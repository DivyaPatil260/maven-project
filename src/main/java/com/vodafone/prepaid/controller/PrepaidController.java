package com.vodafone.prepaid.controller;

import com.vodafone.prepaid.dto.*;
import com.vodafone.prepaid.entity.PrepaidAccount;
import com.vodafone.prepaid.service.PrepaidService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/prepaid")
public class PrepaidController {

    private static final Logger log = LoggerFactory.getLogger(PrepaidController.class);

    private final PrepaidService prepaidService;

    public PrepaidController(PrepaidService prepaidService) {
        this.prepaidService = prepaidService;
    }

    @PostMapping("/account")
    public ResponseEntity<ApiResponse<PrepaidAccount>> createAccount(@Valid @RequestBody PrepaidAccount account) {
        log.info("REST Request: Create Prepaid Account for MSISDN {}", account.getMsisdn());
        PrepaidAccount created = prepaidService.createAccount(account);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Prepaid account created successfully", created));
    }

    @GetMapping("/account/{msisdn}")
    public ResponseEntity<ApiResponse<PrepaidAccount>> getAccount(@PathVariable String msisdn) {
        log.info("REST Request: Get Prepaid Account for MSISDN {}", msisdn);
     //   log.info("This is api calling");
        PrepaidAccount account = prepaidService.getAccountByMsisdn(msisdn);
        return ResponseEntity.ok(ApiResponse.success("Prepaid account retrieved", account));
    }

    @PostMapping("/recharge")
    public ResponseEntity<ApiResponse<PrepaidAccount>> recharge(@Valid @RequestBody RechargeRequest request) {
        log.info("REST Request: Recharge for MSISDN {}", request.getMsisdn());
        PrepaidAccount updated = prepaidService.recharge(request);
        return ResponseEntity.ok(ApiResponse.success("Recharge successful", updated));
    }

    @GetMapping("/feign/postpaid-info/{msisdn}")
    public ResponseEntity<ApiResponse<PostpaidAccountDto>> getPostpaidInfoViaFeign(@PathVariable String msisdn) {
        log.info("REST Request: Fetch Postpaid Info via OpenFeign for MSISDN {}", msisdn);
        PostpaidAccountDto info = prepaidService.getPostpaidInfoViaFeign(msisdn);
        return ResponseEntity.ok(ApiResponse.success("Postpaid info retrieved via Feign", info));
    }

    @GetMapping("/rest/postpaid-bill/{msisdn}")
    public ResponseEntity<ApiResponse<PostpaidBillDto>> getPostpaidBillViaRestTemplate(@PathVariable String msisdn) {
        log.info("REST Request: Fetch Postpaid Bill via RestTemplate for MSISDN {}", msisdn);
        PostpaidBillDto bill = prepaidService.getPostpaidBillViaRestTemplate(msisdn);
        return ResponseEntity.ok(ApiResponse.success("Postpaid bill retrieved via RestTemplate", bill));
    }

    @PostMapping("/migrate-to-postpaid")
    public ResponseEntity<ApiResponse<String>> initiateMigrationSaga(@Valid @RequestBody MigrationRequestDto request) {
        log.info("REST Request: Initiate Plan Migration SAGA for MSISDN {}", request.getMsisdn());
        String transactionId = prepaidService.initiatePlanMigrationSaga(request);
        return ResponseEntity.accepted()
                .body(ApiResponse.success("Plan migration SAGA transaction initiated", transactionId));
    }
}
