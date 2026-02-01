package com.digitalbank.account.service;

import com.digitalbank.account.dto.*;
import com.digitalbank.common.enums.AccountStatus;
import com.digitalbank.common.enums.AccountSubType;
import com.digitalbank.common.enums.AccountType;
import com.digitalbank.common.model.AccountEntity;
import com.digitalbank.account.repository.AccountRepository;
import com.digitalbank.account.repository.CustomerRepository;
import com.digitalbank.common.exception.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.digitalbank.common.utils.AccountConstants.*;


@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final SecureRandom random = new SecureRandom();

    public AccountResponse createAccount(AccountRequest request) {
        String cif = request.getCif();
        if (!customerRepository.existsByCif(cif)) {
            log.warn("Customer not found for CIF : {}", cif);
            throw new CustomerNotFoundException(CUSTOMER_ID_NOT_FOUND + cif);
        }
        String fingerPrint = accountFingerPrint(request);
        Optional<AccountEntity> existing = accountRepository.findByAccountIdentification(fingerPrint);
        if (existing.isPresent()) {
            log.warn("Account already exists for finger Print : {}", fingerPrint);
            throw new ConflictException(ACCOUNT_EXISTS);
        }
        AccountEntity entity = mapRequestToEntity(request);
        entity.setAccountIdentification(fingerPrint);
        entity.setAccountNumber(String.valueOf(generate()));
        entity.setAccountId(UUID.randomUUID().toString());
        AccountEntity saved = accountRepository.save(entity);
        log.info("Account created successfully for account Id -{} ", saved.getAccountId());
        return mapEntityToResponse(saved);
    }


    public AccountResponse findByAccountId(String accountId) {
       log.info("Fetching account details for account Id -{} ", accountId);
        Optional<AccountEntity> entity = accountRepository.findByAccountId(accountId);
        return entity.map(this::mapEntityToResponse).orElseThrow(() -> new AccountNotFoundException(ACCOUNT_NOT_FOUND + accountId));
    }

    public List<AccountResponse> getAccountsList(String cif) {
       log.info("Fetching accounts list for customer Id -{} ", cif);
        Optional<List<AccountEntity>> entities = accountRepository.findByCif(cif);
        List<AccountResponse> accountResponseList;
        if (entities.isPresent()) {
            log.info("Accounts found for customer Id -{} ", cif);
            accountResponseList = entities.get().stream().map(this::mapEntityToResponse).toList();
            return accountResponseList;
        }
        log.info("No accounts found for customer Id -{} ", cif);
        throw new AccountNotFoundException(CUSTOMER_ID_NOT_FOUND + cif);
    }

    public UpdateAccountResponse updateAccount(UpdateAccountRequest request) {
        UpdateAccountResponse response = new UpdateAccountResponse();
        AccountEntity account = accountRepository.findByAccountId(request.getAccountId()).orElseThrow(() -> new AccountNotFoundException(ACCOUNT_NOT_FOUND + request.getAccountId()));
        if (request.getNickname() != null) {
            account.setNickname(request.getNickname());
        }
        if (request.getDisplayName() != null) {
            account.setDisplayName(request.getDisplayName());
        }
        if (request.getAccountType() != null) {
            account.setAccountType(AccountType.valueOf(request.getAccountType()));
        }
        if (request.getAccountSubType() != null) {
            account.setAccountSubType(AccountSubType.valueOf(request.getAccountSubType()));
        }
        AccountEntity updatedAccount = accountRepository.save(account);
        response.setMessage(ACCOUNT_UPDATED_SUCCESSFULLY);
        log.info("Account updated successfully for account Id -{} ", updatedAccount.getAccountId());
        return response;
    }

    public CloseAccountResponse closeAccount(CloseAccountRequest request) {

        String accountId = request.getAccountId();
        AccountEntity account = accountRepository.findByAccountId(accountId).orElseThrow(() -> new AccountNotFoundException("Account not found"));
        log.info("Closing account for account Id -{} ", accountId);
        if (!account.getCif().equals(request.getCif())) {
            throw new CustomerNotFoundException("You cannot close this account, unauthorized CIF");
        }

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new AccountClosedException("Account already closed");
        }

        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new AccountBalanceException("Account balance must be zero to close the account");
        }

        account.setStatus(AccountStatus.CLOSED);
        account.setRemarks(request.getReason());
        account.setClosedAt(LocalDateTime.now());
        accountRepository.save(account);
        CloseAccountResponse response = new CloseAccountResponse();
        response.setAccountId(accountId);
        response.setStatus(CLOSED);
        response.setMessage(CLOSED_SUCCESSFULLY);
        log.info("Account closed successfully for account Id -{} ", accountId);
        return response;
    }

    private AccountResponse mapEntityToResponse(AccountEntity account) {
        log.info("Mapping account entity to response for account Id -{} ", account.getAccountId());
        AccountResponse response = new AccountResponse();
        response.setAccountId(account.getAccountId());
        response.setAccountNumber(account.getAccountNumber());
        response.setAccountType(account.getAccountType());
        response.setAccountSubType(account.getAccountSubType());
        response.setStatus(account.getStatus());
        response.setCif(account.getCif());
        response.setCurrency(account.getCurrency());
        response.setBalance(account.getBalance());
        response.setNickname(account.getNickname());
        response.setDisplayName(account.getDisplayName());
        return response;
    }

    private AccountEntity mapRequestToEntity(AccountRequest request) {
        log.info("Mapping account request to entity for CIF -{} ", request.getCif());
        AccountEntity entity = new AccountEntity();
        entity.setCif(request.getCif());
        entity.setAccountType(request.getAccountType());
        entity.setAccountSubType(request.getAccountSubType());
        entity.setStatus(request.getStatus());
        entity.setNickname(request.getNickname());
        entity.setDisplayName(request.getDisplayName());
        entity.setCurrency(request.getCurrency());
        entity.setBalance(request.getOpeningBalance() == null ? BigDecimal.ZERO : request.getOpeningBalance());
        return entity;
    }

    private long generate() {
        return 1_000_000_000L + random.nextLong(9_000_000_000L);
    }

    private String accountFingerPrint(AccountRequest request) {
        return (request.getCif() + "|" + request.getAccountType() + "|" + request.getAccountSubType() + "|" + request.getCurrency());
    }

}

