package com.digitalbank.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
