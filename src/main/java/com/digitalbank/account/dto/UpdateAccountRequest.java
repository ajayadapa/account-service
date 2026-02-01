package com.digitalbank.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAccountRequest {

    private String accountId;
    private String nickname;
    private String displayName;
    private String accountType;
    private String accountSubType;
}
