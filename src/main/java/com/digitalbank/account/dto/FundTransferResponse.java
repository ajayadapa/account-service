package com.digitalbank.account.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FundTransferResponse {

    private String status;
    private String transactionId;
}
