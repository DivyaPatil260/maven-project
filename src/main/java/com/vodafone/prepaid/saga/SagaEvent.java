package com.vodafone.prepaid.saga;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SagaEvent {

    private String transactionId;
    private String msisdn;
    private String customerName;
    private String targetPlan;
    private BigDecimal creditLimit;
    private String eventType; // MIGRATION_INITIATED, POSTPAID_CREATED, MIGRATION_SUCCESS, MIGRATION_FAILED, COMPENSATE_PREPAID
    private String status;    // PENDING, COMPLETED, FAILED, ROLLED_BACK
    private String reason;
}
