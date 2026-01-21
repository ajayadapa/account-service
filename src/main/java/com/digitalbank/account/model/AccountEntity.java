package com.digitalbank.account.model;

import com.digitalbank.account.dto.AccountStatus;
import com.digitalbank.account.dto.AccountSubType;
import com.digitalbank.account.dto.AccountType;
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
@Table(name = "account", indexes = {@Index(name = "account_customer", columnList = "customerId"),
        @Index(name = "account_status", columnList = "status"),
        @Index(name = "account_identification", columnList = "accountIdentification", unique = true)})
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customerId;

    @Column(length = 20, nullable = false, unique = true)
    private String accountNumber;


    @Column(nullable = false)
    private AccountType accountType;


    @Column(nullable = false)
    private AccountSubType accountSubType;


    @Column(nullable = false)
    private AccountStatus status;

    @Column(length = 3, nullable = false)
    private String currency;

    @Column(length = 64)
    private String nickname;

    @Column(length = 64)
    private String displayName;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal balance;

    @Column(length = 128, unique = true)
    private String accountIdentification;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (balance == null) balance = BigDecimal.ZERO;
    }

    //before updating existing entity
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}



