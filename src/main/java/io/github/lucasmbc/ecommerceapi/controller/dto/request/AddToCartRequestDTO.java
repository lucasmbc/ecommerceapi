package io.github.lucasmbc.ecommerceapi.controller.dto.request;

import java.util.UUID;

public class AddToCartRequestDTO {

    private UUID id;
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
