package io.github.lucasmbc.ecommerceapi.controller.mapper;

import io.github.lucasmbc.ecommerceapi.controller.dto.response.OrderItemResponseDTO;
import io.github.lucasmbc.ecommerceapi.controller.dto.response.OrderResponseDTO;
import io.github.lucasmbc.ecommerceapi.domain.model.Order;
import io.github.lucasmbc.ecommerceapi.domain.model.OrderItem;

import java.util.List;

public class OrderMapper {

    public OrderMapper() {
    }

    public static OrderResponseDTO toDetailResponse(Order order) {
        List<OrderItemResponseDTO> items = order.getItems().stream()
                .map(OrderMapper::mapItem)
                .toList();

        return new OrderResponseDTO(
                order.getId(),
                order.getTotal(),
                order.getStatus().name(),
                order.getOrderDate(),
                items
        );
    }

    private static OrderItemResponseDTO mapItem(OrderItem item) {
        return new OrderItemResponseDTO(
                item.getProduct().getName(),
                item.getQuantity(),
                item.getUnitPrice()
        );
    }
}
