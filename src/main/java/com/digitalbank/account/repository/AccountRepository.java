package com.digitalbank.account.repository;

import com.digitalbank.account.model.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {


    Optional<AccountEntity> findByAccountIdentification(String accountIdentity);

    Optional<List<AccountEntity>> findByCustomerId(String customerId);

    Optional<AccountEntity> findByAccountNumber(String accountNumber);
}
