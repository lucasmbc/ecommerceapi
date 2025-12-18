package io.github.lucasmbc.ecommerceapi.domain.repository;

import io.github.lucasmbc.ecommerceapi.domain.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
}
