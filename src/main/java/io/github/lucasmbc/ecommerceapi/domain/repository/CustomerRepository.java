package io.github.lucasmbc.ecommerceapi.domain.repository;

import io.github.lucasmbc.ecommerceapi.domain.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Optional<Customer> findByEmail(String email);

    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
}
