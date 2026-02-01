package com.digitalbank.account.controller;

import com.digitalbank.account.dto.*;
import com.digitalbank.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/account/api/v1")
public class AccountController {

    private final AccountService accountService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/opening")
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountRequest request) {
        log.info("Account opening request received -{} ", request);
        AccountResponse resp = accountService.createAccount(request);
        return ResponseEntity.ok(resp);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/details/{accountId}")
    public ResponseEntity<AccountResponse> getAccountDetails(@PathVariable String accountId) {
        log.info("Fetching account details for customer by account Id -{} ", accountId);
        AccountResponse response = accountService.findByAccountId(accountId);
        return ResponseEntity.ok().body(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/accounts")
    public ResponseEntity<List<AccountResponse>> getAccountsList(@RequestParam String cif) {
        log.info("Fetching accounts list for customer ");
        return ResponseEntity.ok(accountService.getAccountsList(cif));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/update")
    public ResponseEntity<UpdateAccountResponse> updateAccount(@RequestBody UpdateAccountRequest request) {
        log.info("Updating account details for account Id -{} ", request.getAccountId());
        return ResponseEntity.ok(accountService.updateAccount(request));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/close")
    public ResponseEntity<CloseAccountResponse> closeAccount(@Valid @RequestBody CloseAccountRequest request) {
        log.info("close account for account Id -{} ", request.getAccountId());
        CloseAccountResponse response = accountService.closeAccount(request);
        return ResponseEntity.ok().body(response);
    }

}
