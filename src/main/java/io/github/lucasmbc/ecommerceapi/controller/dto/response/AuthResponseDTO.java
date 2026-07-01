package io.github.lucasmbc.ecommerceapi.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data Transfer Object for authentication response, containing the JWT token.")
public record AuthResponseDTO(
        @Schema(description = "JSON Web Token (JWT) for authentication.", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token
) { }