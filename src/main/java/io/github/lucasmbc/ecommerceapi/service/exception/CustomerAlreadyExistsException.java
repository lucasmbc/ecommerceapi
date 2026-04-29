package io.github.lucasmbc.ecommerceapi.service.exception;

public class CustomerAlreadyExistsException extends BusinessException {
    public CustomerAlreadyExistsException(String email) {
        super("Customer already exists with email: " + email);
    }
}
