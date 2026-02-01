package com.digitalbank.account.dto;

import com.digitalbank.common.enums.AccountStatus;
import com.digitalbank.common.enums.AccountSubType;
import com.digitalbank.common.enums.AccountType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {
    private String cif;
    private String accountId;
    private String accountNumber;
    private AccountType accountType;
    private AccountSubType accountSubType;
    private AccountStatus status;
    private String currency;
    private String nickname;
    private String displayName;
    private BigDecimal balance;
}
