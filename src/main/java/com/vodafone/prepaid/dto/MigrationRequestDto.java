package com.vodafone.prepaid.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MigrationRequestDto {

    @NotBlank(message = "MSISDN is required")
    private String msisdn;

    @NotBlank(message = "Target postpaid plan is required")
    private String targetPostpaidPlan;

    private BigDecimal initialCreditLimit;
}
