package io.github.lucasmbc.ecommerceapi.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Data Transfer Object for category response, containing detailed category information.")
public record CategoryResponseDTO(
        @Schema(description = "Unique identifier of the category.", example = "f1e2d3c4-b5a6-0987-6543-210fedcba987")
        UUID id,
        @Schema(description = "Name of the category.", example = "Electronics")
        String name,
        @Schema(description = "Description of the category.", example = "Category for electronic devices like smartphones, laptops, etc.")
        String description
) {}