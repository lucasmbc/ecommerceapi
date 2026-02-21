package io.github.lucasmbc.ecommerceapi.service.exception;

public class CpfAlreadyExistsException extends BusinessException {
    public CpfAlreadyExistsException(String cpf) {
        super("CPF already registered: " + cpf);
    }
}
