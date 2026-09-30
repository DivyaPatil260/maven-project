package com.vodafone.prepaid.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RechargeRequest {

    @NotBlank(message = "MSISDN is required")
    private String msisdn;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", message = "Recharge amount must be at least 1.00")
    private BigDecimal amount;

    @NotBlank(message = "Plan name is required")
    private String planName;
}
