package io.github.lucasmbc.ecommerceapi.controller.dto.response;

import java.math.BigDecimal;

public record OrderItemResponseDTO(
        String productName,
        Integer quantity,
        BigDecimal unitPrice
) {}
