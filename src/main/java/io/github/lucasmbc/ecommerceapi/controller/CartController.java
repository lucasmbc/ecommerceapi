package io.github.lucasmbc.ecommerceapi.controller;

import io.github.lucasmbc.ecommerceapi.controller.dto.response.CartItemResponseDTO;
import io.github.lucasmbc.ecommerceapi.controller.mapper.CartItemMapper;
import io.github.lucasmbc.ecommerceapi.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    @PostMapping("/items/{productId}")
    public ResponseEntity<Void> addItem(Authentication authentication, @PathVariable String productId, @RequestParam Integer quantity) {
        cartService.addItem(authentication.getName(), UUID.fromString(productId), quantity);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build()
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public List<CartItemResponseDTO> getCart(Authentication authentication) {
        return cartService.getItems(authentication.getName())
                .stream()
                .map(CartItemMapper::toResponse)
                .toList();
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeItem(Authentication authentication, @PathVariable UUID productId) {
        cartService.removeItem(authentication.getName(), productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        cartService.delete(authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
