package com.digitalbank.account.repository;

import com.digitalbank.account.model.Idempotency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyRepository
        extends JpaRepository<Idempotency, String> {
}

