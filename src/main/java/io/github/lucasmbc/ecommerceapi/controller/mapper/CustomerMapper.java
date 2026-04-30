package io.github.lucasmbc.ecommerceapi.controller.mapper;

import io.github.lucasmbc.ecommerceapi.controller.dto.request.CustomerRequestDTO;
import io.github.lucasmbc.ecommerceapi.controller.dto.response.CustomerResponseDTO;
import io.github.lucasmbc.ecommerceapi.domain.model.Customer;
import io.github.lucasmbc.ecommerceapi.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerMapper() {
    }

    public static Customer toEntity(CustomerRequestDTO dto, User user) {
        Customer customerDTO = new Customer();
        customerDTO.setName(dto.getName());
        customerDTO.setCpf(dto.getCpf());
        customerDTO.setPhone(dto.getPhone());
        customerDTO.setAddress(dto.getAddress());
        customerDTO.setUser(user);
        return customerDTO;
    }

    public static CustomerResponseDTO toResponse(Customer customer) {
        return new CustomerResponseDTO(
                customer.getId(),
                customer.getName(),
                customer.getUser().getEmail(),
                customer.getCpf(),
                customer.getPhone(),
                customer.getAddress()
        );
    }
}
