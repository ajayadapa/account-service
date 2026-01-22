package com.digitalbank.account.service;

import com.digitalbank.account.model.Idempotency;
import com.digitalbank.account.repository.IdempotencyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final IdempotencyRepository repo;
    private final ObjectMapper objectMapper;

    public <T> ResponseEntity<T> handle(String key, Class<T> responseType, Supplier<ResponseEntity<T>> action) {

        return repo.findById(key).map(entity -> {
            try {
                T body = objectMapper.readValue(entity.getResponseJson(), responseType);

                return ResponseEntity.status(entity.getResponseStatus()).body(body);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).orElseGet(() -> {
            ResponseEntity<T> response = action.get();
            try {
                Idempotency entity = new Idempotency();
                entity.setIdempotencyKey(key);
                entity.setResponseStatus(response.getStatusCode().value());
                entity.setResponseJson(objectMapper.writeValueAsString(response.getBody()));
                repo.save(entity);
                return response;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}

