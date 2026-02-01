package com.digitalbank.account.account.tests;

import com.digitalbank.account.controller.AccountController;
import com.digitalbank.account.dto.*;
import com.digitalbank.account.service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    @Test
    void createAccount_success() {
        AccountRequest request = new AccountRequest();
        AccountResponse response = new AccountResponse();
        when(accountService.createAccount(any(AccountRequest.class))).thenReturn(response);
        ResponseEntity<AccountResponse> result = accountController.createAccount(request);
        assertEquals(200, result.getStatusCode().value());
        assertEquals(response, result.getBody());
        verify(accountService, times(1)).createAccount(request);
    }

    @Test
    void getAccountDetails_success() {
        String accountId = "acc-123";
        AccountResponse response = new AccountResponse();
        when(accountService.findByAccountId(accountId)).thenReturn(response);
        ResponseEntity<AccountResponse> result = accountController.getAccountDetails(accountId);
        assertEquals(200, result.getStatusCode().value());
        assertEquals(response, result.getBody());
        verify(accountService).findByAccountId(accountId);
    }

    @Test
    void getAccountsList_success() {
        String cif = "cif-001";
        List<AccountResponse> responses = List.of(new AccountResponse());
        when(accountService.getAccountsList(cif)).thenReturn(responses);
        ResponseEntity<List<AccountResponse>> result = accountController.getAccountsList(cif);
        assertEquals(200, result.getStatusCode().value());
        assertEquals(responses, result.getBody());
        verify(accountService).getAccountsList(cif);
    }

    @Test
    void updateAccount_success() {
        UpdateAccountRequest request = new UpdateAccountRequest();
        UpdateAccountResponse response = new UpdateAccountResponse();
        when(accountService.updateAccount(any(UpdateAccountRequest.class))).thenReturn(response);
        ResponseEntity<UpdateAccountResponse> result = accountController.updateAccount(request);
        assertEquals(200, result.getStatusCode().value());
        assertEquals(response, result.getBody());
        verify(accountService).updateAccount(request);
    }

    @Test
    void closeAccount_success() {
        CloseAccountRequest request = new CloseAccountRequest();
        CloseAccountResponse response = new CloseAccountResponse();
        when(accountService.closeAccount(any(CloseAccountRequest.class))).thenReturn(response);
        ResponseEntity<CloseAccountResponse> result = accountController.closeAccount(request);
        assertEquals(200, result.getStatusCode().value());
        assertEquals(response, result.getBody());
        verify(accountService).closeAccount(request);
    }
}
