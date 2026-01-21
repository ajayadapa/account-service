package com.digitalbank.account.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccountDetailsByNumberRequest {
    @NotBlank
    private String accountNumber;


}


