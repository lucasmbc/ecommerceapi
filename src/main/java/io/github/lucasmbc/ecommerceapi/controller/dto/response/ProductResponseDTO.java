package io.github.lucasmbc.ecommerceapi.controller.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponseDTO(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        String imageUrl,
        UUID categoryId,
        String categoryName
) { }
