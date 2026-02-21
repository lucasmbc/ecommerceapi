package io.github.lucasmbc.ecommerceapi.controller.mapper;

import io.github.lucasmbc.ecommerceapi.controller.dto.response.CartItemResponseDTO;
import io.github.lucasmbc.ecommerceapi.domain.model.CartItem;

public class CartItemMapper {

    private CartItemMapper() {}

    public static CartItemResponseDTO toResponse(CartItem item) {
        return new CartItemResponseDTO(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getUnitPrice()
        );
    }
}
