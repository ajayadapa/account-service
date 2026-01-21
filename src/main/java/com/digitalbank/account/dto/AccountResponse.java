package com.digitalbank.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {
    private String customerId;
    private String accountNumber;
    private AccountType accountType;
    private AccountSubType accountSubType;
    private AccountStatus status;
    private String currency;
    private String nickname;
    private String displayName;
    private BigDecimal balance;
}
