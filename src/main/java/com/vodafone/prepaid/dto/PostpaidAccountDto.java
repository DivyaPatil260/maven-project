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
public class PostpaidAccountDto {

    private Long id;
    private String msisdn;
    private String customerName;
    private BigDecimal creditLimit;
    private BigDecimal unbilledAmount;
    private String planName;
    private String status;
}
