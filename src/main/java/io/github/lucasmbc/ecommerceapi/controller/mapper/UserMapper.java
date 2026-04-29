package io.github.lucasmbc.ecommerceapi.controller.mapper;

import io.github.lucasmbc.ecommerceapi.controller.dto.request.RegisterRequestDTO;
import io.github.lucasmbc.ecommerceapi.domain.model.User;

public class UserMapper {

    private UserMapper() {}

    public static User toEntity(RegisterRequestDTO dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        return user;
    }
}
