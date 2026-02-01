package com.digitalbank.account.repository;

import com.digitalbank.common.model.Idempotency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyRepository
        extends JpaRepository<Idempotency, String> {
}

