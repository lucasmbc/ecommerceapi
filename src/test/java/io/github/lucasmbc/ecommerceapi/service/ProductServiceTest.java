package io.github.lucasmbc.ecommerceapi.service;

import io.github.lucasmbc.ecommerceapi.controller.dto.request.ProductRequestDTO;
import io.github.lucasmbc.ecommerceapi.domain.model.Category;
import io.github.lucasmbc.ecommerceapi.domain.model.Product;
import io.github.lucasmbc.ecommerceapi.domain.repository.CategoryRepository;
import io.github.lucasmbc.ecommerceapi.domain.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private Product savedProduct;
    private ProductRequestDTO dto;
    private Category category;

    @BeforeEach
    public void setup() {
        category = new Category();
        category.setId(UUID.randomUUID());
        category.setName("Electronics");

        dto = new ProductRequestDTO();
        dto.setName("iPhone");
        dto.setDescription("iPhone description");
        dto.setPrice(BigDecimal.valueOf(3000));
        dto.setStock(10);
        dto.setCategoryId(category.getId());

        savedProduct = new Product();
        savedProduct.setName(dto.getName());
        savedProduct.setDescription(dto.getDescription());
        savedProduct.setPrice(dto.getPrice());
        savedProduct.setStock(dto.getStock());
        savedProduct.setCategory(category);
    }

    @Test
    @DisplayName("JUnit test should create product")
    void shouldCreateProduct() {
        given(productRepository.save(any(Product.class))).willReturn(savedProduct);
        given(categoryRepository.findById(any(UUID.class))).willReturn(Optional.of(category));

        Product createdProduct = productService.create(dto);

        assertNotNull(createdProduct);
        assertEquals(createdProduct.getName(), savedProduct.getName());
        assertEquals(createdProduct.getDescription(), savedProduct.getDescription());
        assertEquals(createdProduct.getPrice(), savedProduct.getPrice());
        assertEquals(createdProduct.getStock(), savedProduct.getStock());
        assertEquals(createdProduct.getCategory().getName(), savedProduct.getCategory().getName());
    }

    @Test
    @DisplayName("JUnit test should find product by id")
    void shouldFindProductById() {
        given(productRepository.findById(savedProduct.getId())).willReturn(Optional.of(savedProduct));

        Product foundProduct = productService.findById(savedProduct.getId());

        assertNotNull(foundProduct);
        assertEquals(foundProduct.getName(), savedProduct.getName());
    }

    @Test
    @DisplayName("JUnit test should find all products")
    void shouldFindAllProducts() {
        Product newProduct = new Product();
        newProduct.setName("MacBook");
        newProduct.setDescription("MacBook description");
        newProduct.setPrice(BigDecimal.valueOf(5000));
        newProduct.setStock(5);

        given(productRepository.findAll()).willReturn(List.of(savedProduct, newProduct));

        List<Product> foundProducts = productService.findAll();

        assertNotNull(foundProducts);
        assertEquals(2, foundProducts.size());
    }

    @Test
    @DisplayName("JUnit test should update product")
    void shouldUpdateProduct() {
        given(productRepository.findById(savedProduct.getId())).willReturn(Optional.of(savedProduct));
        given(categoryRepository.findById(any(UUID.class))).willReturn(Optional.of(category));

        savedProduct.setName("iPhone 14");
        savedProduct.setDescription("iPhone 14 description");
        savedProduct.setPrice(BigDecimal.valueOf(3500));
        savedProduct.setStock(15);

        given(productRepository.save(savedProduct)).willReturn(savedProduct);

        Product updatedProduct = productService.update(savedProduct.getId(), dto);

        assertNotNull(updatedProduct);
        assertEquals(updatedProduct.getName(), savedProduct.getName());
        assertEquals(updatedProduct.getDescription(), savedProduct.getDescription());
        assertEquals(updatedProduct.getPrice(), savedProduct.getPrice());
        assertEquals(updatedProduct.getStock(), savedProduct.getStock());
    }

    @Test
    @DisplayName("JUnit test should delete product")
    void shouldDeleteProduct() {
        given(productRepository.findById(savedProduct.getId())).willReturn(Optional.of(savedProduct));
        willDoNothing().given(productRepository).deleteById(savedProduct.getId());

        productService.delete(savedProduct.getId());

        verify(productRepository, times(1)).deleteById(savedProduct.getId());
    }
}