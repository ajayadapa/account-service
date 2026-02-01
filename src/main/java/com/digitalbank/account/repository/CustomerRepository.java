package com.digitalbank.account.repository;

import com.digitalbank.common.model.CustomerEntity;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

    boolean existsByCif(String cif);
}
