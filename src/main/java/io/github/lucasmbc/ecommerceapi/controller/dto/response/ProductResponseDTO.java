package io.github.lucasmbc.ecommerceapi.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Data Transfer Object for product response, containing detailed product information.")
public record ProductResponseDTO(
        @Schema(description = "Unique identifier of the product.", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
        UUID id,

        @Schema(description = "Name of the product.", example = "Smartphone X")
        String name,

        @Schema(description = "Detailed description of the product.", example = "A high-performance smartphone with advanced camera features.")
        String description,

        @Schema(description = "Price of the product.", example = "999.99")
        BigDecimal price,

        @Schema(description = "Current stock quantity available for the product.", example = "150")
        Integer stock,

        @Schema(description = "URL to the product's image.", example = "http://example.com/images/smartphone_x.jpg")
        String imageUrl,

        @Schema(description = "Unique identifier of the category the product belongs to.", example = "f1e2d3c4-b5a6-0987-6543-210fedcba987")
        UUID categoryId,

        @Schema(description = "Name of the category the product belongs to.", example = "Electronics")
        String categoryName
) { }