package com.digitalbank.account.service;

import com.digitalbank.account.dto.FundTransferRequest;
import com.digitalbank.account.dto.FundTransferResponse;
import com.digitalbank.account.repository.AccountRepository;
import com.digitalbank.account.repository.TransactionRepository;
import com.digitalbank.common.exception.AccountNotFoundException;
import com.digitalbank.common.exception.InsufficientBalanceException;
import com.digitalbank.common.model.AccountEntity;
import com.digitalbank.common.model.TransactionEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.digitalbank.common.utils.AccountConstants.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class FundTransferService {


    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public FundTransferResponse fundTransfer(FundTransferRequest request) {

        AccountEntity sourceAccount = accountRepository.findByAccountNumber(request.getSourceAccountId()).orElseThrow(() -> new AccountNotFoundException(DEBIT_ACCOUNT_NOT_FOUND));
        AccountEntity destinationAccount = accountRepository.findByAccountNumber(request.getDestAccountId()).orElseThrow(() -> new AccountNotFoundException(CREDIT_ACCOUNT_NOT_FOUND));
        String transactionFingerPrint = requestFingerPrint(request);
        Optional<TransactionEntity> entity = transactionRepository.findByRequestFingerPrint(transactionFingerPrint);
        FundTransferResponse response = new FundTransferResponse();
        if (entity.isPresent()) {
            log.debug("Duplicate transaction detected for fingerprint: {}", transactionFingerPrint);
            response.setStatus(DUPLICATE_TRAN);
            response.setTransactionId(String.valueOf(entity.get().getId()));
            return response;
        }
        BigDecimal availableSourceAmount = sourceAccount.getBalance();
        if (request.getAmount().compareTo(availableSourceAmount) > 0) {
            log.debug("Insufficient funds in source account: {}. Available: {}, Requested: {}", sourceAccount.getAccountNumber(), availableSourceAmount, request.getAmount());
            throw new InsufficientBalanceException(INSUFFICIENT_FUNDS);
        }
        return emitTransaction(request, response, sourceAccount, destinationAccount, transactionFingerPrint);
    }

    private String requestFingerPrint(FundTransferRequest request) {

        return request.getSourceAccountId().toLowerCase() +
                request.getDestAccountId().toLowerCase() +
                request.getAmount().toPlainString() +
                request.getCurrency().toLowerCase() +
                request.getReason().toLowerCase() +
                request.getType().toLowerCase();
    }


    private FundTransferResponse emitTransaction(FundTransferRequest request, FundTransferResponse response, AccountEntity sourceAccount, AccountEntity destinationAccount, String transactionFingerPrint) {

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.getAmount()));
        destinationAccount.setBalance(destinationAccount.getBalance().add(request.getAmount()));
        accountRepository.saveAll(List.of(sourceAccount, destinationAccount));
        TransactionEntity requestToEntity = mapRequestToEntity(request);
        requestToEntity.setRequestFingerPrint(transactionFingerPrint);
        TransactionEntity savedTransaction = transactionRepository.save(requestToEntity);
        log.debug("Transaction successful with id: {}", savedTransaction.getId());
        response.setTransactionId(String.valueOf(savedTransaction.getId()));
        response.setStatus(SUCCESS);
        return response;
    }

    private TransactionEntity mapRequestToEntity(FundTransferRequest request) {
        log.info(" Mapping fund transfer request to transaction entity");
        TransactionEntity entity = new TransactionEntity();
        entity.setDebitAccount(request.getSourceAccountId());
        entity.setCreditAccount(request.getDestAccountId());
        entity.setAmount(request.getAmount());
        entity.setCurrency(request.getCurrency());
        entity.setStatus(SUCCESS);
        entity.setReason(request.getReason());
        entity.setType(request.getType());
        return entity;
    }
}
