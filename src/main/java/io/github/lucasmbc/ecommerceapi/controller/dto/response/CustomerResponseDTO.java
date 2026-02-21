package io.github.lucasmbc.ecommerceapi.controller.dto.response;

import java.util.UUID;

public record CustomerResponseDTO(
        UUID id,
        String name,
        String email,
        String cpf,
        String phone,
        String address
) {}
