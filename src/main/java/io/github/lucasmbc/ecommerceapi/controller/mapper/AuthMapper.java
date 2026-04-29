package io.github.lucasmbc.ecommerceapi.controller.mapper;

import io.github.lucasmbc.ecommerceapi.controller.dto.response.AuthResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public static AuthResponseDTO toResponse(String token) {
        return new AuthResponseDTO(token);
    }
}
