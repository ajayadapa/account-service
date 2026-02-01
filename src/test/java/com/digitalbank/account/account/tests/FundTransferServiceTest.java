package com.digitalbank.account.account.tests;

import com.digitalbank.account.dto.FundTransferRequest;
import com.digitalbank.account.dto.FundTransferResponse;
import com.digitalbank.account.repository.AccountRepository;
import com.digitalbank.account.repository.TransactionRepository;
import com.digitalbank.account.service.FundTransferService;
import com.digitalbank.common.exception.AccountNotFoundException;
import com.digitalbank.common.exception.InsufficientBalanceException;
import com.digitalbank.common.model.AccountEntity;
import com.digitalbank.common.model.TransactionEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.digitalbank.common.utils.AccountConstants.DUPLICATE_TRAN;
import static com.digitalbank.common.utils.AccountConstants.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FundTransferServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private FundTransferService fundTransferService;

    /* ---------- SUCCESS CASE ---------- */

    @Test
    void fundTransfer_success() {
        FundTransferRequest request = new FundTransferRequest();
        request.setSourceAccountId("SRC-1");
        request.setDestAccountId("DEST-1");
        request.setAmount(BigDecimal.valueOf(500));
        request.setCurrency("INR");

        AccountEntity source = new AccountEntity();
        source.setAccountNumber("SRC-1");
        source.setBalance(BigDecimal.valueOf(1000));

        AccountEntity destination = new AccountEntity();
        destination.setAccountNumber("DEST-1");
        destination.setBalance(BigDecimal.valueOf(200));

        TransactionEntity savedTxn = new TransactionEntity();
        savedTxn.setId(1L);

        when(accountRepository.findByAccountNumber("SRC-1"))
                .thenReturn(Optional.of(source));
        when(accountRepository.findByAccountNumber("DEST-1"))
                .thenReturn(Optional.of(destination));
        when(transactionRepository.findByRequestFingerPrint(any()))
                .thenReturn(Optional.empty());
        when(transactionRepository.save(any()))
                .thenReturn(savedTxn);

        FundTransferResponse response =
                fundTransferService.fundTransfer(request);

        assertEquals(SUCCESS, response.getStatus());
        assertEquals("1", response.getTransactionId());
        assertEquals(BigDecimal.valueOf(500), source.getBalance());
        assertEquals(BigDecimal.valueOf(700), destination.getBalance());

        verify(accountRepository).saveAll(List.of(source, destination));
        verify(transactionRepository).save(any());
    }

    /* ---------- DUPLICATE TRANSACTION ---------- */

    @Test
    void fundTransfer_duplicateTransaction_returnsDuplicateStatus() {
        FundTransferRequest request = new FundTransferRequest();
        request.setSourceAccountId("SRC-1");
        request.setDestAccountId("DEST-1");
        request.setAmount(BigDecimal.valueOf(100));

        AccountEntity source = new AccountEntity();
        source.setBalance(BigDecimal.valueOf(500));

        AccountEntity destination = new AccountEntity();
        destination.setBalance(BigDecimal.valueOf(300));

        TransactionEntity existingTxn = new TransactionEntity();
        existingTxn.setId(99L);

        when(accountRepository.findByAccountNumber("SRC-1"))
                .thenReturn(Optional.of(source));
        when(accountRepository.findByAccountNumber("DEST-1"))
                .thenReturn(Optional.of(destination));
        when(transactionRepository.findByRequestFingerPrint(any()))
                .thenReturn(Optional.of(existingTxn));

        FundTransferResponse response =
                fundTransferService.fundTransfer(request);

        assertEquals(DUPLICATE_TRAN, response.getStatus());
        assertEquals("99", response.getTransactionId());

        verify(transactionRepository, never()).save(any());
        verify(accountRepository, never()).saveAll(any());
    }

    /* ---------- ACCOUNT NOT FOUND ---------- */

    @Test
    void fundTransfer_whenSourceAccountMissing_throwsException() {
        FundTransferRequest request = new FundTransferRequest();
        request.setSourceAccountId("SRC-1");

        when(accountRepository.findByAccountNumber("SRC-1"))
                .thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> fundTransferService.fundTransfer(request));
    }

    /* ---------- INSUFFICIENT BALANCE ---------- */

    @Test
    void fundTransfer_whenInsufficientBalance_throwsException() {
        FundTransferRequest request = new FundTransferRequest();
        request.setSourceAccountId("SRC-1");
        request.setDestAccountId("DEST-1");
        request.setAmount(BigDecimal.valueOf(1000));

        AccountEntity source = new AccountEntity();
        source.setBalance(BigDecimal.valueOf(200));

        AccountEntity destination = new AccountEntity();
        destination.setBalance(BigDecimal.valueOf(500));

        when(accountRepository.findByAccountNumber("SRC-1"))
                .thenReturn(Optional.of(source));
        when(accountRepository.findByAccountNumber("DEST-1"))
                .thenReturn(Optional.of(destination));
        when(transactionRepository.findByRequestFingerPrint(any()))
                .thenReturn(Optional.empty());

        assertThrows(InsufficientBalanceException.class,
                () -> fundTransferService.fundTransfer(request));
    }
}
