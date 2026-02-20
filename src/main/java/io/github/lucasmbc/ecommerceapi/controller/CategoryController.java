package io.github.lucasmbc.ecommerceapi.controller;

import io.github.lucasmbc.ecommerceapi.controller.dto.request.CategoryRequestDTO;
import io.github.lucasmbc.ecommerceapi.controller.dto.response.CategoryResponseDTO;
import io.github.lucasmbc.ecommerceapi.controller.mapper.CategoryMapper;
import io.github.lucasmbc.ecommerceapi.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> create(@Valid @RequestBody CategoryRequestDTO dto) {
        var saved = categoryService.create(CategoryMapper.toEntity(dto));
        var categoryResponseDTO = CategoryMapper.toResponse(saved);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).body(categoryResponseDTO);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> findAll() {
        var categories = categoryService.findAll();
        var categoryResponseDto = categories.stream().map(CategoryMapper::toResponse).toList();
        return ResponseEntity.ok(categoryResponseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> findById(@PathVariable String id) {
        var category = categoryService.findById(UUID.fromString(id));
        return ResponseEntity.ok(CategoryMapper.toResponse(category));
    }

    @PutMapping("/{id}")
    public  ResponseEntity<CategoryResponseDTO> update(@PathVariable String id, @RequestBody CategoryRequestDTO dto) {
        var category = categoryService.update(UUID.fromString(id), CategoryMapper.toEntity(dto));
        return ResponseEntity.ok(CategoryMapper.toResponse(category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        categoryService.delete(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }
}
