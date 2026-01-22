package com.digitalbank.account.controller;

import com.digitalbank.account.dto.FundTransferRequest;
import com.digitalbank.account.dto.FundTransferResponse;
import com.digitalbank.account.service.FundTransferService;
import com.digitalbank.account.service.IdempotencyService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/transfer/api/v1")
public class FundTransferController {

    @Autowired
    private FundTransferService fundTransferService;

    @Autowired
    private IdempotencyService idempotencyService;

    @PostMapping("/internal")
    public ResponseEntity<FundTransferResponse> fundTransfer(@RequestHeader("Idempotency-Key") String key, @Valid @RequestBody FundTransferRequest request) {

        log.info("fund transfer request received -{} ", request);
        return idempotencyService.handle(
                key, FundTransferResponse.class, () -> ResponseEntity.ok(fundTransferService.fundTransfer(request)
                )
        );

    }
}
