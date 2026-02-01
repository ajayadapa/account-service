package com.digitalbank.account.service;

import com.digitalbank.account.repository.IdempotencyRepository;
import com.digitalbank.common.exception.IdempotencyCheckException;
import com.digitalbank.common.model.Idempotency;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

import static com.digitalbank.common.utils.AccountConstants.DUPLICATE_TRANSACTION;
import static com.digitalbank.common.utils.AccountConstants.IDEMPOTENCY_EXCEPTION;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final IdempotencyRepository repo;
    private final ObjectMapper objectMapper;

    public <T> ResponseEntity<T> handle(String key, Supplier<ResponseEntity<T>> action) {

        if (repo.existsById(key)) {
            throw new IdempotencyCheckException(DUPLICATE_TRANSACTION);
        }
        ResponseEntity<T> response = action.get();
        try {
            Idempotency entity = new Idempotency();
            entity.setIdempotencyKey(key);
            entity.setResponseStatus(response.getStatusCode().value());
            entity.setResponseJson(objectMapper.writeValueAsString(response.getBody()));
            repo.save(entity);
            return response;
        } catch (Exception e) {
            throw new IdempotencyCheckException(IDEMPOTENCY_EXCEPTION);
        }
    }
}

