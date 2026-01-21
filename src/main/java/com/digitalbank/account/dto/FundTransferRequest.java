package com.digitalbank.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FundTransferRequest {

    @NotBlank
    private String sourceAccountId;
    @NotBlank
    private String destAccountId;
    @NotNull
    @Positive
    private BigDecimal amount;
    @NotBlank
    private String currency;
    @NotBlank
    private String type;
    @NotBlank
    private String reason;


}
