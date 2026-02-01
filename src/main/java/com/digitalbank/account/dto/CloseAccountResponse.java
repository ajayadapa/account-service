package com.digitalbank.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CloseAccountResponse {
    private String message;
    private String accountId;
    private String status;
    private LocalDateTime closedAt;
}

