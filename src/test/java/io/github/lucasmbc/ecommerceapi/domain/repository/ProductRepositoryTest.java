package io.github.lucasmbc.ecommerceapi.domain.repository;

import io.github.lucasmbc.ecommerceapi.domain.model.Category;
import io.github.lucasmbc.ecommerceapi.domain.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;
    private Product product;

    @BeforeEach
    public void setup() {
        category = new Category();
        category.setName("Books");

        product = new Product();
        product.setName("Java Book");
        product.setPrice(BigDecimal.valueOf(100));
        product.setStock(10);
        product.setCategory(category);
    }

    @DisplayName("JUnit test should find products by categoryId")
    @Test
    void shouldFindProductsByCategoryId() {
        categoryRepository.save(category);
        productRepository.save(product);

        List<Product> products = productRepository.findByCategoryId(category.getId());

        assertNotNull(products);
        assertEquals(1, products.size());
    }

    @DisplayName("JUnit test should find by name containing ignore case")
    @Test
    void findByNameContainingIgnoreCase() {
        Product product = new Product();
        product.setName("Spring Boot Course");
        product.setPrice(BigDecimal.valueOf(200));
        product.setStock(5);
        product.setCategory(category);

        categoryRepository.save(category);
        productRepository.save(product);

        List<Product> products = productRepository.findByNameContainingIgnoreCase("spring");

        assertFalse(products.isEmpty());
    }
}