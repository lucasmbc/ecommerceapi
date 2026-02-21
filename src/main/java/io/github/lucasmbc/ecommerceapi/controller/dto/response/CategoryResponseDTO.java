package io.github.lucasmbc.ecommerceapi.controller.dto.response;

import java.util.UUID;

public record CategoryResponseDTO(
        UUID id,
        String name,
        String description
) {}
