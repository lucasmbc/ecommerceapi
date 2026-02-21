package io.github.lucasmbc.ecommerceapi.service.exception;

public class NotFoundException extends BusinessException{
    public NotFoundException(){
        super("Resource not found");
    }

    public NotFoundException(String message){
        super(message);
    }
}
