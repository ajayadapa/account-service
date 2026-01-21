package com.digitalbank.account.controller;

import com.digitalbank.account.dto.FundTransferRequest;
import com.digitalbank.account.dto.FundTransferResponse;
import com.digitalbank.account.service.FundTransferService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/transfer/api/v1")
public class FundTransferController {

    @Autowired
    private FundTransferService fundTransferService;

    @PostMapping("/internal")
    public ResponseEntity<FundTransferResponse> fundTransfer(@Valid @RequestBody FundTransferRequest request) {
        log.info("fund transfer request received -{} ", request);
        FundTransferResponse response = fundTransferService.fundTransfer(request);
        return ResponseEntity.ok(response);
    }
}
