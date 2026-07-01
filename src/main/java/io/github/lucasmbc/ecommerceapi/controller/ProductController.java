package io.github.lucasmbc.ecommerceapi.controller;

import io.github.lucasmbc.ecommerceapi.controller.dto.request.ProductRequestDTO;
import io.github.lucasmbc.ecommerceapi.controller.dto.response.ProductResponseDTO;
import io.github.lucasmbc.ecommerceapi.controller.mapper.ProductMapper;
import io.github.lucasmbc.ecommerceapi.domain.model.Product;
import io.github.lucasmbc.ecommerceapi.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "Products API")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create a new product", description = "Create a new product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductRequestDTO dto) {

        Product saved = productService.create(dto);

        ProductResponseDTO productResponseDTO = ProductMapper.toResponse(saved);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).body(productResponseDTO);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    @Operation(summary = "Find all products", description = "Find all products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products found"),
            @ApiResponse(responseCode = "404", description = "Products not found")
    })
    public ResponseEntity<List<ProductResponseDTO>> findAll() {
        List<Product> products = productService.findAll();
        var productResponseDto = products.stream().map(ProductMapper::toResponse).toList();
        return ResponseEntity.ok(productResponseDto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{id}")
    @Operation(summary = "Find product by ID", description = "Find product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductResponseDTO> findById(@PathVariable String id) {
        var idProduct = UUID.fromString(id);
        Product product = productService.findById(idProduct);
        return ResponseEntity.ok(ProductMapper.toResponse(product));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update a product", description = "Update a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductResponseDTO> update(@PathVariable String id, @RequestBody ProductRequestDTO dto) {
        var product = productService.update(UUID.fromString(id), dto);
        return ResponseEntity.ok(ProductMapper.toResponse(product));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product", description = "Delete a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> delete(@PathVariable String id) {
        productService.delete(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }
}
