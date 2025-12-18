package io.github.lucasmbc.ecommerceapi.domain.repository;

import io.github.lucasmbc.ecommerceapi.domain.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
