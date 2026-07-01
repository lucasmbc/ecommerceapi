package io.github.lucasmbc.ecommerceapi.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;

@Schema(description = "Data Transfer Object for customer creation or update requests.")
public class CustomerRequestDTO {

    @NotBlank(message = "Name must not be blank")
    @Schema(description = "Full name of the customer.", example = "Maria Silva")
    private String name;

    @NotBlank(message = "CPF must not be blank")
    @CPF
    @Schema(description = "Brazilian CPF (Cadastro de Pessoas Físicas) of the customer.", example = "123.456.789-00")
    private String cpf;

    @Schema(description = "Phone number of the customer.", example = "(11) 98765-4321")
    private String phone;

    @Schema(description = "Full address of the customer.", example = "Rua Exemplo, 123 - Bairro Centro, Cidade, Estado")
    private String address;

    public CustomerRequestDTO() {
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}