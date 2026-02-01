package com.digitalbank.account.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CloseAccountRequest {

    @NotNull
    private String cif;
    @NotNull
    private String accountId;
    @NotNull
    private String reason;

}
