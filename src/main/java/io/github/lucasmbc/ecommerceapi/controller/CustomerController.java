package io.github.lucasmbc.ecommerceapi.controller;

import io.github.lucasmbc.ecommerceapi.controller.dto.request.CustomerRequestDTO;
import io.github.lucasmbc.ecommerceapi.controller.dto.response.CustomerResponseDTO;
import io.github.lucasmbc.ecommerceapi.controller.mapper.CustomerMapper;
import io.github.lucasmbc.ecommerceapi.domain.model.Customer;
import io.github.lucasmbc.ecommerceapi.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> create(@Valid @RequestBody CustomerRequestDTO request) {
        Customer customer = CustomerMapper.toEntity(request);

        Customer saved = customerService.create(customer);

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

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> findById(@PathVariable String id) {
        var customer = customerService.findById(UUID.fromString(id));
        return ResponseEntity.ok(CustomerMapper.toResponse(customer));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> update(@PathVariable String id, @RequestBody CustomerRequestDTO request) {
        var customer = customerService.update(UUID.fromString(id), CustomerMapper.toEntity(request));
        return ResponseEntity.ok(CustomerMapper.toResponse(customer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        customerService.delete(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }
}
