package io.github.lucasmbc.ecommerceapi.controller;

import io.github.lucasmbc.ecommerceapi.controller.dto.request.CustomerRequestDTO;
import io.github.lucasmbc.ecommerceapi.controller.dto.response.CustomerResponseDTO;
import io.github.lucasmbc.ecommerceapi.controller.mapper.CustomerMapper;
import io.github.lucasmbc.ecommerceapi.domain.model.Customer;
import io.github.lucasmbc.ecommerceapi.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/customers")
@Tag(name = "Customers", description = "Customers API")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping
    @Operation(summary = "Create a new customer", description = "Create a new customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "CPF already registered")
    })
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Find all customers", description = "Find all customers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "404", description = "Customers not found")
    })
    public ResponseEntity<List<CustomerResponseDTO>> findAll() {
        List<Customer> customers = customerService.findAll();
        var customerResponseDTOs = customers.stream().map((CustomerMapper::toResponse)).toList();
        return ResponseEntity.ok(customerResponseDTOs);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{email}")
    @Operation(summary = "Find customer by email", description = "Find customer by email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<CustomerResponseDTO> findByEmail(@PathVariable String email) {
        var customer = customerService.findByEmail(email);
        return ResponseEntity.ok(CustomerMapper.toResponse(customer));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping
    @Operation(summary = "Update a customer", description = "Update a customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer updated"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<CustomerResponseDTO> update(
            Authentication authentication,
            @RequestBody CustomerRequestDTO request
    ) {
        var customer = customerService.update(authentication.getName(), request);

        return ResponseEntity.ok(CustomerMapper.toResponse(customer));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping
    @Operation(summary = "Delete a customer", description = "Delete a customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Customer deleted"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<Void> delete(Authentication authentication) {
        customerService.delete(authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
