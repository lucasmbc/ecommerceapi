package io.github.lucasmbc.ecommerceapi.controller;

import io.github.lucasmbc.ecommerceapi.controller.dto.response.CartItemResponseDTO;
import io.github.lucasmbc.ecommerceapi.controller.mapper.CartItemMapper;
import io.github.lucasmbc.ecommerceapi.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/carts")
@Tag(name = "Carts", description = "Carts API")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/items/{productId}")
    @Operation(summary = "Add item to cart", description = "Add item to cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item added"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Product or customer not found")
    })
    public ResponseEntity<Void> addItem(Authentication authentication, @PathVariable String productId, @RequestParam Integer quantity) {
        cartService.addItem(authentication.getName(), UUID.fromString(productId), quantity);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build()
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    @Operation(summary = "Get cart items", description = "Get cart items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart items found"),
            @ApiResponse(responseCode = "404", description = "Cart not found")
    })
    public List<CartItemResponseDTO> getCart(Authentication authentication) {
        return cartService.getItems(authentication.getName())
                .stream()
                .map(CartItemMapper::toResponse)
                .toList();
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping("/items/{productId}")
    @Operation(summary = "Remove item from cart", description = "Remove item from cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Item removed"),
            @ApiResponse(responseCode = "404", description = "Cart or product not found")
    })
    public ResponseEntity<Void> removeItem(Authentication authentication, @PathVariable UUID productId) {
        cartService.removeItem(authentication.getName(), productId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping
    @Operation(summary = "Clear cart", description = "Clear cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cart cleared"),
            @ApiResponse(responseCode = "404", description = "Cart not found")
    })
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        cartService.delete(authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
