package io.github.lucasmbc.ecommerceapi.service.exception;

public class EmailAlreadyExistsException extends BusinessException{
    public EmailAlreadyExistsException(String email) {
        super("Email already registered: " + email);
    }
}
