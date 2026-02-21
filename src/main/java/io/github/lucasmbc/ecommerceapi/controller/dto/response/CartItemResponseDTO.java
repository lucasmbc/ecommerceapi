package io.github.lucasmbc.ecommerceapi.controller.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemResponseDTO(
        UUID productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice
) {}
