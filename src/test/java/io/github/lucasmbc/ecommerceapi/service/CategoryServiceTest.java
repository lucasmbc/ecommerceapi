package io.github.lucasmbc.ecommerceapi.service;

import io.github.lucasmbc.ecommerceapi.domain.model.Category;
import io.github.lucasmbc.ecommerceapi.domain.repository.CategoryRepository;
import io.github.lucasmbc.ecommerceapi.service.exception.BusinessException;
import io.github.lucasmbc.ecommerceapi.service.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(UUID.randomUUID());
        category.setName("Category 1");
    }

    @DisplayName("JUnit test should create category")
    @Test
    void shouldCreateCategory() {
        given(categoryRepository.save(category)).willReturn(category);

        Category saved = categoryService.create(category);

        assertNotNull(saved);
        assertEquals(category.getName(), saved.getName());
    }

    @DisplayName("JUnit test should throw exception when category exists")
    @Test
    void shouldThrowExceptionWhenCategoryExists() {
        given(categoryRepository.findByNameIgnoreCase(anyString())).willReturn(Optional.of(category));

        assertThrows(BusinessException.class, () -> categoryService.create(category));

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @DisplayName("JUnit test should find category by id")
    @Test
    void shouldFindCategoryById() {
        given(categoryRepository.findById(category.getId())).willReturn(Optional.of(category));

        Category saved = categoryService.findById(category.getId());

        assertNotNull(saved);
        assertEquals(category.getName(), saved.getName());
    }

    @DisplayName("JUnit test should throw exception when category id not found")
    @Test
    void shouldThrowExceptionWhenCategoryNotFound() {
        given(categoryRepository.findById(category.getId())).willThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> categoryService.findById(category.getId()));

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @DisplayName("JUnit test should find all category")
    @Test
    void shouldFindAllCategories() {
        Category category2 = new Category();
        category2.setId(UUID.randomUUID());
        category2.setName("Category 2");

        given(categoryRepository.findAll()).willReturn(List.of(category, category2));

        List<Category> categories = categoryService.findAll();

        assertNotNull(categories);
        assertEquals(2, categories.size());
    }

    @DisplayName("JUnit test should return throw exception when empty category list")
    @Test
    void shouldReturnEmptyCategoryList() {
        given(categoryRepository.findAll()).willReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> categoryService.findAll());
    }

    @DisplayName("JUnit test should update category")
    @Test
    void shouldUpdateCategory() {
        given(categoryRepository.findById(category.getId())).willReturn(Optional.of(category));

        category.setName("Updated Category 1");
        category.setDescription("Updated Description 1");

        given(categoryRepository.save(category)).willReturn(category);

        Category saved = categoryService.update(category.getId(), category);

        assertNotNull(saved);
        assertEquals(category.getName(), saved.getName());
        assertEquals(category.getDescription(), saved.getDescription());
    }

    @DisplayName("JUnit test should delete category")
    @Test
    void shouldDeleteCategory() {
        given(categoryRepository.findById(category.getId())).willReturn(Optional.of(category));
        willDoNothing().given(categoryRepository).deleteById(category.getId());

        categoryService.delete(category.getId());

        verify(categoryRepository, times(1)).deleteById(category.getId());
    }
}