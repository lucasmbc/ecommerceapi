package io.github.lucasmbc.ecommerceapi.service.exception;

public class CustomBadRequestException extends BusinessException {
    public CustomBadRequestException(String message){
        super(message);
    }
}
