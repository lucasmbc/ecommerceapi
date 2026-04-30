package io.github.lucasmbc.ecommerceapi.controller;

import io.github.lucasmbc.ecommerceapi.controller.dto.request.CustomerRequestDTO;
import io.github.lucasmbc.ecommerceapi.controller.dto.response.CustomerResponseDTO;
import io.github.lucasmbc.ecommerceapi.controller.mapper.CustomerMapper;
import io.github.lucasmbc.ecommerceapi.domain.model.Customer;
import io.github.lucasmbc.ecommerceapi.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> create(
            @Valid @RequestBody CustomerRequestDTO request,
            Authentication authentication
    ) {

        Customer saved = customerService.create(request, authentication.getName());

        CustomerResponseDTO customerResponseDTO = CustomerMapper.toResponse(saved);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).body(customerResponseDTO);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> findAll() {
        List<Customer> customers = customerService.findAll();
        var customerResponseDTOs = customers.stream().map((CustomerMapper::toResponse)).toList();
        return ResponseEntity.ok(customerResponseDTOs);
    }

    @GetMapping("/{email}")
    public ResponseEntity<CustomerResponseDTO> findByEmail(@PathVariable String email) {
        var customer = customerService.findByEmail(email);
        return ResponseEntity.ok(CustomerMapper.toResponse(customer));
    }

    @PutMapping
    public ResponseEntity<CustomerResponseDTO> update(
            Authentication authentication,
            @RequestBody CustomerRequestDTO request
    ) {
        var customer = customerService.update(authentication.getName(), request);

        return ResponseEntity.ok(CustomerMapper.toResponse(customer));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(Authentication authentication) {
        customerService.delete(authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
