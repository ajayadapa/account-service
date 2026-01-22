package com.digitalbank.account.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@Entity
@Table(name = "idempotency")
public class Idempotency {

    @Id
    @Column(length = 64)
    private String idempotencyKey;

    private int responseStatus;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String responseJson;

    private LocalDateTime createdAt = LocalDateTime.now();
}

