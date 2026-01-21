package com.digitalbank.account.repository;

import com.digitalbank.account.model.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository <TransactionEntity, Long>{
    Optional<TransactionEntity> findByRequestFingerPrint(String transactionFingerPrint);
}
