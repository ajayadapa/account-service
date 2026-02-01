package com.digitalbank.account.dto;


import com.digitalbank.common.enums.AccountStatus;
import com.digitalbank.common.enums.AccountSubType;
import com.digitalbank.common.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequest {

    @NotBlank
    private String cif;

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

