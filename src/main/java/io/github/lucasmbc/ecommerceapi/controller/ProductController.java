package io.github.lucasmbc.ecommerceapi.controller;

import io.github.lucasmbc.ecommerceapi.controller.dto.request.ProductRequestDTO;
import io.github.lucasmbc.ecommerceapi.controller.dto.response.ProductResponseDTO;
import io.github.lucasmbc.ecommerceapi.controller.mapper.ProductMapper;
import io.github.lucasmbc.ecommerceapi.domain.model.Product;
import io.github.lucasmbc.ecommerceapi.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
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

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> findAll() {
        List<Product> products = productService.findAll();
        var productResponseDto = products.stream().map(ProductMapper::toResponse).toList();
        return ResponseEntity.ok(productResponseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> findById(@PathVariable String id) {
        var idProduct = UUID.fromString(id);
        Product product = productService.findById(idProduct);
        return ResponseEntity.ok(ProductMapper.toResponse(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(@PathVariable String id, @RequestBody ProductRequestDTO dto) {
        var product = productService.update(UUID.fromString(id), dto);
        return ResponseEntity.ok(ProductMapper.toResponse(product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        productService.delete(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }
}
