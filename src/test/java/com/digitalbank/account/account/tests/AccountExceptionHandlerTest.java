package com.digitalbank.account.account.tests;

import com.digitalbank.account.controller.AccountExceptionHandler;
import com.digitalbank.common.exception.AccountNotFoundException;
import com.digitalbank.common.exception.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AccountExceptionHandlerTest {

    private AccountExceptionHandler exceptionHandler;

    @Mock
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        exceptionHandler = new AccountExceptionHandler();
        when(webRequest.getDescription(false)).thenReturn("test-request");
    }

    @Test
    void handleAllExceptions_shouldReturnInternalServerError() throws Exception {
        Exception ex = new Exception("Unexpected error");

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleAllExceptions(ex, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Unexpected error", response.getBody().getMessage());
    }

    @Test
    void accountNotFoundException_shouldReturnNotFound() throws Exception {
        AccountNotFoundException ex =
                new AccountNotFoundException("Account not found");

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.accountNotFoundException(ex, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Account not found", response.getBody().getMessage());
    }
}
