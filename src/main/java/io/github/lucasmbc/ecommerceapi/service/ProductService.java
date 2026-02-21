package io.github.lucasmbc.ecommerceapi.service;

import io.github.lucasmbc.ecommerceapi.controller.dto.request.ProductRequestDTO;
import io.github.lucasmbc.ecommerceapi.controller.mapper.ProductMapper;
import io.github.lucasmbc.ecommerceapi.domain.model.Category;
import io.github.lucasmbc.ecommerceapi.domain.model.Product;
import io.github.lucasmbc.ecommerceapi.domain.repository.CategoryRepository;
import io.github.lucasmbc.ecommerceapi.domain.repository.ProductRepository;
import io.github.lucasmbc.ecommerceapi.service.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public Product create(ProductRequestDTO dto) {

        Category category = categoryRepository.findById(dto.getCategoryId()).orElseThrow(() -> new NotFoundException("Category not found"));

        Product product = ProductMapper.toEntity(dto, category);

        return productRepository.save(product);
    }

    public Product findById(UUID id) {
        return productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product not found"));
    }

    public List<Product> findAll() {
        List<Product> products = productRepository.findAll();
        if(products.isEmpty()) throw new NotFoundException("Products not found");
        return products;
    }

    public Product update(UUID id, ProductRequestDTO product) {
        Product dbProduct =  productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product not found"));
        Category dbCategory = categoryRepository.findById(product.getCategoryId()).orElseThrow(() -> new NotFoundException("Category not found"));

        dbProduct.setName(product.getName());
        dbProduct.setDescription(product.getDescription());
        dbProduct.setPrice(product.getPrice());
        dbProduct.setStock(product.getStock());
        dbProduct.setImageUrl(product.getImageUrl());
        dbProduct.setCategory(dbCategory);

        return productRepository.save(dbProduct);
    }

    public void delete(UUID id) {
        Product dbProduct =  productRepository.findById(id).orElseThrow(NotFoundException::new);

        productRepository.deleteById(dbProduct.getId());
    }

}
