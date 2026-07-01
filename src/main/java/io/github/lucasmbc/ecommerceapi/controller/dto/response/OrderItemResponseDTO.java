package io.github.lucasmbc.ecommerceapi.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Data Transfer Object for order item response, containing details of a product within an order.")
public record OrderItemResponseDTO(
        @Schema(description = "Name of the product in the order.", example = "Smartphone X")
        String productName,
        @Schema(description = "Quantity of the product in the order.", example = "1")
        Integer quantity,
        @Schema(description = "Unit price of the product at the time the order was placed.", example = "999.99")
        BigDecimal unitPrice
) {}