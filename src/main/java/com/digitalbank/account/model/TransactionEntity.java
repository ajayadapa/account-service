package com.digitalbank.account.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 20, nullable = false)
    private String debitAccount;
    @Column(length = 20, nullable = false)
    private String creditAccount;
    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;
    @Column(nullable = false)
    private String reason;
    @Column(length = 20, nullable = false)
    private String type;
    @Column(length = 3, nullable = false)
    private String currency;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private String requestFingerPrint;
    @Column(nullable = false)
    private String status;
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
