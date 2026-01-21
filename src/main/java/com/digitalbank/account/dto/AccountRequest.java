package com.digitalbank.account.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequest {

    @NotBlank
    private String customerId;

    @NotNull
    private AccountType accountType;

    @NotNull
    private AccountSubType accountSubType;

    @NotNull
    private AccountStatus status;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{3}$")
    private String currency;

    private String nickname;

    private String displayName;

    @PositiveOrZero
    private BigDecimal openingBalance;
}

