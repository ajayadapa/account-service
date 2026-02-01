package com.digitalbank.account.account.tests;

import com.digitalbank.account.controller.FundTransferController;
import com.digitalbank.account.dto.FundTransferRequest;
import com.digitalbank.account.dto.FundTransferResponse;
import com.digitalbank.common.exception.InsufficientBalanceException;
import com.digitalbank.account.service.IdempotencyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FundTransferControllerTest {

    @Mock
    private IdempotencyService idempotencyService;

    @InjectMocks
    private FundTransferController fundTransferController;

    @Test
    void fundTransfer_success() {

        FundTransferRequest request = new FundTransferRequest();
        request.setSourceAccountId("ACC-1");
        request.setDestAccountId("ACC-2");
        request.setAmount(BigDecimal.valueOf(100));
        request.setCurrency("INR");

        FundTransferResponse response = new FundTransferResponse();
        response.setTransactionId("TXN-001");
        response.setStatus("SUCCESS");

        when(idempotencyService.handle(anyString(), any(Supplier.class))).thenReturn(ResponseEntity.ok(response));

        ResponseEntity<FundTransferResponse> result = fundTransferController.fundTransfer("idem-key-123", request);
        assertEquals(200, result.getStatusCode().value());
        assertEquals("SUCCESS", result.getBody().getStatus());
        assertEquals("TXN-001", result.getBody().getTransactionId());
    }

    @Test
    void fundTransfer_shouldThrowException_whenFundTransferFails() {

        String idempotencyKey = "key-123";
        FundTransferRequest request = new FundTransferRequest();
        when(idempotencyService.handle(eq(idempotencyKey), any())).thenThrow(new InsufficientBalanceException("Insufficient funds"));
        assertThrows(InsufficientBalanceException.class, () -> fundTransferController.fundTransfer(idempotencyKey, request));
    }

    @Test
    void fundTransfer_shouldThrowException_whenIdempotencyFails() {

        String key = "key-123";
        FundTransferRequest request = new FundTransferRequest();
        when(idempotencyService.handle(anyString(), any())).thenThrow(new RuntimeException("Idempotency error"));
        assertThrows(RuntimeException.class, () -> fundTransferController.fundTransfer(key, request));
    }


}


