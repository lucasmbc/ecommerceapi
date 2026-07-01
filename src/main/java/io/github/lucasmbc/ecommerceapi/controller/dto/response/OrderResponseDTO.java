package io.github.lucasmbc.ecommerceapi.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Data Transfer Object for order response, containing detailed order information.")
public record OrderResponseDTO(
        @Schema(description = "Unique identifier of the order.", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
        UUID id,
        @Schema(description = "Total amount of the order.", example = "250.75")
        BigDecimal total,
        @Schema(description = "Current status of the order (e.g., PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED).", example = "PENDING")
        String status,
        @Schema(description = "Date and time when the order was placed.", example = "2023-10-26T10:30:00")
        LocalDateTime orderDate,
        @Schema(description = "List of items included in the order.")
        List<OrderItemResponseDTO> items
) {}