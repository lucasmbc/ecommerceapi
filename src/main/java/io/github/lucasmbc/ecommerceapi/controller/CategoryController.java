package io.github.lucasmbc.ecommerceapi.controller;

import io.github.lucasmbc.ecommerceapi.controller.dto.request.CategoryRequestDTO;
import io.github.lucasmbc.ecommerceapi.controller.dto.response.CategoryResponseDTO;
import io.github.lucasmbc.ecommerceapi.controller.mapper.CategoryMapper;
import io.github.lucasmbc.ecommerceapi.service.CategoryService;
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
@RequestMapping("/categories")
@Tag(name = "Categories", description = "Categories API")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create a new category", description = "Create a new category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "422", description = "Category already exists")
    })
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

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    @Operation(summary = "Find all categories", description = "Find all categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories found"),
            @ApiResponse(responseCode = "404", description = "Categories not found")
    })
    public ResponseEntity<List<CategoryResponseDTO>> findAll() {
        var categories = categoryService.findAll();
        var categoryResponseDto = categories.stream().map(CategoryMapper::toResponse).toList();
        return ResponseEntity.ok(categoryResponseDto);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Find category by ID", description = "Find category by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category found"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<CategoryResponseDTO> findById(@PathVariable String id) {
        var category = categoryService.findById(UUID.fromString(id));
        return ResponseEntity.ok(CategoryMapper.toResponse(category));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update a category", description = "Update a category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public  ResponseEntity<CategoryResponseDTO> update(@PathVariable String id, @RequestBody CategoryRequestDTO dto) {
        var category = categoryService.update(UUID.fromString(id), CategoryMapper.toEntity(dto));
        return ResponseEntity.ok(CategoryMapper.toResponse(category));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category", description = "Delete a category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category deleted"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<Void> delete(@PathVariable String id) {
        categoryService.delete(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }
}
