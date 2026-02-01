package com.digitalbank.account.repository;

import com.digitalbank.common.model.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    Optional<AccountEntity> findByAccountIdentification(String accountIdentity);

    Optional<AccountEntity> findByAccountNumber(String accountNumber);

    Optional<AccountEntity> findByAccountId(String accountId);

    Optional<List<AccountEntity>> findByCif(String cif);
}
