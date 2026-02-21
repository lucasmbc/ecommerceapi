package io.github.lucasmbc.ecommerceapi.controller.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponseDTO(
        UUID id,
        BigDecimal total,
        String status,
        LocalDateTime orderDate,
        List<OrderItemResponseDTO> items
) {}
