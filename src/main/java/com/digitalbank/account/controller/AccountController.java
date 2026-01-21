package com.digitalbank.account.controller;

import com.digitalbank.account.dto.AccountDetailsByNumberRequest;
import com.digitalbank.account.dto.AccountRequest;
import com.digitalbank.account.dto.AccountResponse;
import com.digitalbank.account.dto.AccountsListByCustomerIdRequest;
import com.digitalbank.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/account/api/v1")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/opening")
    public ResponseEntity<AccountResponse> create(@Valid @RequestBody AccountRequest request) {
        log.info("Account opening request received -{} ", request);
        AccountResponse resp = accountService.create(request);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("customer/account/details")
    public ResponseEntity<AccountResponse> getAccountDetails(@Valid @RequestBody AccountDetailsByNumberRequest request) {
        log.info("Fetching account details for customer ");
        AccountResponse response = accountService.findByAccountNumber(request.getAccountNumber());
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/customer/accounts")
    public ResponseEntity<List<AccountResponse>> getAccountsList(@Valid @RequestBody AccountsListByCustomerIdRequest request) {
        log.info("Fetching accounts list for customer ");
        return ResponseEntity.ok(accountService.findByCustomerId(request.getCustomerId()));
    }
}
