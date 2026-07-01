package io.github.lucasmbc.ecommerceapi.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Data Transfer Object for cart item response, containing details of a product in the cart.")
public record CartItemResponseDTO(
        @Schema(description = "Unique identifier of the product in the cart.", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
        UUID productId,
        @Schema(description = "Name of the product in the cart.", example = "Smartphone X")
        String productName,
        @Schema(description = "Quantity of the product in the cart.", example = "2")
        Integer quantity,
        @Schema(description = "Unit price of the product at the time it was added to the cart.", example = "999.99")
        BigDecimal unitPrice
) {}