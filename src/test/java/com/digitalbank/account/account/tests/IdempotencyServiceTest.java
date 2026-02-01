package com.digitalbank.account.account.tests;

import com.digitalbank.common.model.Idempotency;
import com.digitalbank.account.repository.IdempotencyRepository;
import com.digitalbank.common.exception.IdempotencyCheckException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.digitalbank.account.service.IdempotencyService;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdempotencyServiceTest {

    @Mock
    private IdempotencyRepository repo;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private IdempotencyService idempotencyService;

    @Test
    void handle_existingKey_returnsCachedResponse() throws Exception {

        Idempotency entity = new Idempotency();
        entity.setIdempotencyKey("key-123");
        entity.setResponseStatus(200);
        entity.setResponseJson("{\"status\":\"SUCCESS\"}");

        when(repo.findById("key-123"))
                .thenReturn(Optional.of(entity));
        when(objectMapper.readValue(entity.getResponseJson(), String.class))
                .thenReturn("SUCCESS");

        ResponseEntity<String> response =
                idempotencyService.handle(
                        "key-123",
                        () -> ResponseEntity.ok("NEW")
                );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("SUCCESS", response.getBody());

        verify(repo, never()).save(any());
    }

    @Test
    void handle_newKey_executesActionAndSaves() throws Exception {

        when(repo.findById("key-999"))
                .thenReturn(Optional.empty());

        when(objectMapper.writeValueAsString("SUCCESS"))
                .thenReturn("{\"status\":\"SUCCESS\"}");

        Supplier<ResponseEntity<String>> action =
                () -> ResponseEntity.status(HttpStatus.CREATED).body("SUCCESS");

        ResponseEntity<String> response =
                idempotencyService.handle("key-999",  action);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("SUCCESS", response.getBody());

        verify(repo).save(any(Idempotency.class));
    }

    @Test
    void handle_jsonException_shouldThrowIdempotencyCheckException() throws Exception {

        Idempotency entity = new Idempotency();
        entity.setResponseJson("invalid-json");

        when(repo.findById("key-error"))
                .thenReturn(Optional.of(entity));
        when(objectMapper.readValue(
                anyString(),
                any(Class.class)
        )).thenThrow(new RuntimeException());

        assertThrows(
                IdempotencyCheckException.class,
                () -> idempotencyService.handle(
                        "key-error",
                        () -> ResponseEntity.ok("OK")
                )
        );
    }
}
