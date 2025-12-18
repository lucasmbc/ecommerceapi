package io.github.lucasmbc.ecommerceapi.domain.repository;

import io.github.lucasmbc.ecommerceapi.domain.model.Order;
import io.github.lucasmbc.ecommerceapi.domain.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findByCustomerId(UUID customerId);

    List<Order> findByStatus(OrderStatus status);
}
