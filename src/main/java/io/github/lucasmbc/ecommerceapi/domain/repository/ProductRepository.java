package io.github.lucasmbc.ecommerceapi.domain.repository;

import io.github.lucasmbc.ecommerceapi.domain.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findByCategoryId(UUID categoryId);

    List<Product> findByNameContainingIgnoreCase(String name);
}
