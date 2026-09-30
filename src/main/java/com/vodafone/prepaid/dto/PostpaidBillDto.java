package com.vodafone.prepaid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostpaidBillDto {

    private String msisdn;
    private String customerName;
    private BigDecimal currentUnbilled;
    private BigDecimal creditLimit;
    private BigDecimal availableCredit;
}
