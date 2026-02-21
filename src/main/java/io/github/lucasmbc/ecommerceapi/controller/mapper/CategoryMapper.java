package io.github.lucasmbc.ecommerceapi.controller.mapper;

import io.github.lucasmbc.ecommerceapi.controller.dto.request.CategoryRequestDTO;
import io.github.lucasmbc.ecommerceapi.controller.dto.response.CategoryResponseDTO;
import io.github.lucasmbc.ecommerceapi.domain.model.Category;

public class CategoryMapper {

    public CategoryMapper() {
    }

    public static Category toEntity(CategoryRequestDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        return category;
    }

    public static CategoryResponseDTO toResponse(Category category) {
        return new CategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
}
