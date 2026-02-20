package io.github.lucasmbc.ecommerceapi.controller;

import io.github.lucasmbc.ecommerceapi.controller.dto.response.CartItemResponseDTO;
import io.github.lucasmbc.ecommerceapi.controller.mapper.CartItemMapper;
import io.github.lucasmbc.ecommerceapi.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/{customerId}/items/{productId}")
    public ResponseEntity<Void> addItem(@PathVariable String customerId, @PathVariable String productId, @RequestParam Integer quantity) {
        cartService.addItem(UUID.fromString(customerId), UUID.fromString(productId), quantity);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build()
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{customerId}")
    public List<CartItemResponseDTO> getCart(@PathVariable String customerId) {
        return cartService.getItems(UUID.fromString(customerId))
                .stream()
                .map(CartItemMapper::toResponse)
                .toList();
    }
}
