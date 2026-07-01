package io.github.lucasmbc.ecommerceapi.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Data Transfer Object for customer response, containing detailed customer information.")
public record CustomerResponseDTO(
        @Schema(description = "Unique identifier of the customer.", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
        UUID id,

        @Schema(description = "Full name of the customer.", example = "Maria Silva")
        String name,

        @Schema(description = "Email address of the customer.", example = "maria.silva@example.com")
        String email,

        @Schema(description = "Brazilian CPF (Cadastro de Pessoas Físicas) of the customer.", example = "123.456.789-00")
        String cpf,

        @Schema(description = "Phone number of the customer.", example = "(11) 98765-4321")
        String phone,

        @Schema(description = "Full address of the customer.", example = "Rua Exemplo, 123 - Bairro Centro, Cidade, Estado")
        String address
) {}