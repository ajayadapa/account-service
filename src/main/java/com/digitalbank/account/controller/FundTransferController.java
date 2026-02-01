package com.digitalbank.account.controller;

import com.digitalbank.account.dto.FundTransferRequest;
import com.digitalbank.account.dto.FundTransferResponse;
import com.digitalbank.account.service.FundTransferService;
import com.digitalbank.account.service.IdempotencyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/transfer/api/v1")
public class FundTransferController {


    private final FundTransferService fundTransferService;
    private final IdempotencyService idempotencyService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/internal")
    public ResponseEntity<FundTransferResponse> fundTransfer(@RequestHeader("Idempotency-Key") String key, @Valid @RequestBody FundTransferRequest request) {

        log.info("fund transfer request received -{} ", request);
        return idempotencyService.handle(key, () -> ResponseEntity.ok(fundTransferService.fundTransfer(request)));
    }
}
