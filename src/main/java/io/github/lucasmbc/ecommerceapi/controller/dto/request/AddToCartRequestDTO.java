package io.github.lucasmbc.ecommerceapi.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Data Transfer Object for adding a product to the cart.")
public class AddToCartRequestDTO {

    @Schema(description = "Unique identifier of the product to be added to the cart.", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
    private UUID id;
    @Schema(description = "Quantity of the product to add to the cart.", example = "1")
    private int quantity;

    public AddToCartRequestDTO() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}