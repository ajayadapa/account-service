package com.digitalbank.account.service;

import com.digitalbank.account.dto.AccountRequest;
import com.digitalbank.account.dto.AccountResponse;
import com.digitalbank.account.model.AccountEntity;
import com.digitalbank.account.repository.AccountRepository;
import com.digitalbank.dto.exception.customer.AccountNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

import static com.digitalbank.account.constants.Constants.ACCOUNT_NOT_FOUND;
import static com.digitalbank.account.constants.Constants.CUSTOMER_ID_NOT_FOUND;

@Slf4j
@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    private final SecureRandom random = new SecureRandom();

    public AccountResponse create(AccountRequest request) {

        String fingerPrint = accountFingerPrint(request);
        Optional<AccountEntity> existing = accountRepository.findByAccountIdentification(fingerPrint);
        if (existing.isPresent()) {
            log.info("Account already exists for fingerPrinty: {}", fingerPrint);
            AccountEntity account = existing.get();
            return mapEntityToResponse(account);
        }
        AccountEntity entity = mapRequestToEntity(request);
        entity.setAccountIdentification(fingerPrint);
        entity.setBalance(request.getOpeningBalance() == null ? BigDecimal.ZERO : request.getOpeningBalance());
        entity.setAccountNumber(String.valueOf(generate()));
        AccountEntity saved = accountRepository.save(entity);
        return mapEntityToResponse(saved);
    }

    private String accountFingerPrint(AccountRequest request) {
        return (request.getCustomerId() + "|" + request.getAccountType() + "|" + request.getAccountSubType() + "|" + request.getCurrency() + "|" + request.getNickname() + "|" + request.getDisplayName().toUpperCase());
    }

    public AccountResponse findByAccountNumber(String accountNumber) {
        Optional<AccountEntity> entity = accountRepository.findByAccountNumber(accountNumber);
        return entity.map(this::mapEntityToResponse).orElseThrow(() -> new AccountNotFoundException(ACCOUNT_NOT_FOUND + accountNumber));
    }

    public List<AccountResponse> findByCustomerId(String customerId) {
        Optional<List<AccountEntity>> entities = accountRepository.findByCustomerId(customerId);
        List<AccountResponse> accountResponseList;
        if (entities.isPresent()) {
            accountResponseList = entities.get().stream().map(this::mapEntityToResponse).toList();
            return accountResponseList;
        }
        throw new AccountNotFoundException(CUSTOMER_ID_NOT_FOUND + customerId);
    }

    private AccountResponse mapEntityToResponse(AccountEntity account) {
        AccountResponse response = new AccountResponse();
        response.setAccountNumber(account.getAccountNumber());
        response.setAccountType(account.getAccountType());
        response.setAccountSubType(account.getAccountSubType());
        response.setStatus(account.getStatus());
        response.setCustomerId(account.getCustomerId());
        response.setCurrency(account.getCurrency());
        response.setBalance(account.getBalance());
        response.setNickname(account.getNickname());
        response.setDisplayName(account.getDisplayName());
        return response;
    }

    private AccountEntity mapRequestToEntity(AccountRequest request) {
        AccountEntity entity = new AccountEntity();
        entity.setCustomerId(request.getCustomerId());
        entity.setAccountType(request.getAccountType());
        entity.setAccountSubType(request.getAccountSubType());
        entity.setStatus(request.getStatus());
        entity.setNickname(request.getNickname());
        entity.setDisplayName(request.getDisplayName());
        entity.setCurrency(request.getCurrency());
        return entity;
    }

    private long generate() {
        return 1_000_000_000L + random.nextLong(9_000_000_000L);
    }

}

