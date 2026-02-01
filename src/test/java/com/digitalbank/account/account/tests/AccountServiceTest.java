package com.digitalbank.account.account.tests;

import com.digitalbank.account.dto.*;
import com.digitalbank.account.repository.AccountRepository;
import com.digitalbank.account.service.AccountService;
import com.digitalbank.common.enums.AccountStatus;
import com.digitalbank.common.enums.AccountSubType;
import com.digitalbank.common.enums.AccountType;
import com.digitalbank.common.exception.AccountBalanceException;
import com.digitalbank.common.exception.AccountClosedException;
import com.digitalbank.common.exception.AccountNotFoundException;
import com.digitalbank.common.model.AccountEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.digitalbank.common.utils.AccountConstants.CLOSED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    /* ---------- CREATE ACCOUNT ---------- */

    @Test
    void createAccountAccount_whenAccountAlreadyExists_returnsExistingAccount() {
        AccountRequest request = new AccountRequest();
        request.setCif("CIF1");
        request.setDisplayName("john");
        request.setAccountType(AccountType.SAVINGS);
        request.setAccountSubType(AccountSubType.PERSONAL);
        request.setCurrency("INR");

        AccountEntity entity = new AccountEntity();
        entity.setAccountId("acc-1");

        when(accountRepository.findByAccountIdentification(any())).thenReturn(Optional.of(entity));

        AccountResponse response = accountService.createAccount(request);

        assertEquals("acc-1", response.getAccountId());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void createAccountAccount_whenNewAccount_savesAndReturnsAccount() {
        AccountRequest request = new AccountRequest();
        request.setCif("CIF1");
        request.setDisplayName("john");
        request.setAccountType(AccountType.SAVINGS);
        request.setAccountSubType(AccountSubType.PERSONAL);
        request.setCurrency("INR");

        when(accountRepository.findByAccountIdentification(any())).thenReturn(Optional.empty());
        when(accountRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AccountResponse response = accountService.createAccount(request);

        assertNotNull(response.getAccountId());
        assertNotNull(response.getAccountNumber());
        verify(accountRepository).save(any());
    }

    /* ---------- FIND BY ACCOUNT ID ---------- */

    @Test
    void findByAccountId_whenFound_returnsAccount() {
        AccountEntity entity = new AccountEntity();
        entity.setAccountId("acc-123");

        when(accountRepository.findByAccountId("acc-123")).thenReturn(Optional.of(entity));

        AccountResponse response = accountService.findByAccountId("acc-123");

        assertEquals("acc-123", response.getAccountId());
    }

    @Test
    void findByAccountId_whenNotFound_throwsException() {
        when(accountRepository.findByAccountId("acc-123")).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.findByAccountId("acc-123"));
    }

    /* ---------- GET ACCOUNTS LIST ---------- */

    @Test
    void getAccountsList_whenFound_returnsList() {
        AccountEntity entity = new AccountEntity();
        entity.setAccountId("acc-1");

        when(accountRepository.findByCif("CIF1")).thenReturn(Optional.of(List.of(entity)));

        List<AccountResponse> responses = accountService.getAccountsList("CIF1");

        assertEquals(1, responses.size());
    }

    @Test
    void getAccountsList_whenNotFound_throwsException() {
        when(accountRepository.findByCif("CIF1")).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.getAccountsList("CIF1"));
    }

    /* ---------- UPDATE ACCOUNT ---------- */

    @Test
    void updateAccount_whenAccountExists_updatesSuccessfully() {
        UpdateAccountRequest request = new UpdateAccountRequest();
        request.setAccountId("acc-1");
        request.setNickname("newNick");

        AccountEntity entity = new AccountEntity();
        entity.setAccountId("acc-1");

        when(accountRepository.findByAccountId("acc-1")).thenReturn(Optional.of(entity));
        when(accountRepository.save(any())).thenReturn(entity);

        UpdateAccountResponse response = accountService.updateAccount(request);

        assertNotNull(response);
        verify(accountRepository).save(entity);
    }

    @Test
    void updateAccount_whenNotFound_throwsException() {
        UpdateAccountRequest request = new UpdateAccountRequest();
        request.setAccountId("acc-1");

        when(accountRepository.findByAccountId("acc-1")).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.updateAccount(request));
    }

    /* ---------- CLOSE ACCOUNT ---------- */

    @Test
    void closeAccount_success() {
        CloseAccountRequest request = new CloseAccountRequest();
        request.setAccountId("acc-1");
        request.setCif("CIF1");
        request.setReason("customer request");

        AccountEntity entity = new AccountEntity();
        entity.setAccountId("acc-1");
        entity.setCif("CIF1");
        entity.setStatus(AccountStatus.ACTIVE);
        entity.setBalance(BigDecimal.ZERO);

        when(accountRepository.findByAccountId("acc-1")).thenReturn(Optional.of(entity));

        CloseAccountResponse response = accountService.closeAccount(request);

        assertEquals("acc-1", response.getAccountId());
        assertEquals(CLOSED, response.getStatus());
        verify(accountRepository).save(entity);
    }

    @Test
    void closeAccount_whenBalanceNotZero_throwsException() {
        CloseAccountRequest request = new CloseAccountRequest();
        request.setAccountId("acc-1");
        request.setCif("CIF1");

        AccountEntity entity = new AccountEntity();
        entity.setAccountId("acc-1");
        entity.setCif("CIF1");
        entity.setBalance(BigDecimal.TEN);

        when(accountRepository.findByAccountId("acc-1")).thenReturn(Optional.of(entity));

        assertThrows(AccountBalanceException.class, () -> accountService.closeAccount(request));
    }

    @Test
    void closeAccount_whenAlreadyClosed_throwsException() {
        CloseAccountRequest request = new CloseAccountRequest();
        request.setAccountId("acc-1");
        request.setCif("CIF1");

        AccountEntity entity = new AccountEntity();
        entity.setAccountId("acc-1");
        entity.setCif("CIF1");
        entity.setStatus(AccountStatus.CLOSED);
        entity.setBalance(BigDecimal.ZERO);

        when(accountRepository.findByAccountId("acc-1")).thenReturn(Optional.of(entity));

        assertThrows(AccountClosedException.class, () -> accountService.closeAccount(request));
    }
}
