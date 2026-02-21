package io.github.lucasmbc.ecommerceapi.service;

import io.github.lucasmbc.ecommerceapi.domain.model.Category;
import io.github.lucasmbc.ecommerceapi.domain.repository.CategoryRepository;
import io.github.lucasmbc.ecommerceapi.service.exception.BusinessException;
import io.github.lucasmbc.ecommerceapi.service.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Category create(Category category) {
        if (categoryRepository.findByNameIgnoreCase(category.getName()).isPresent()) {
            throw new BusinessException("Category with name " + category.getName() + " already exists");
        }
        return categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public Category findById(UUID id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category not found"));
    }

    @Transactional(readOnly = true)
    public List<Category> findAll() {
        List<Category> categories = categoryRepository.findAll();
        if(categories.isEmpty()) throw new NotFoundException("Categories not found");
        return categories;
    }

    @Transactional
    public Category update(UUID id, Category category) {
        Category dbCategory = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category not found"));

        dbCategory.setName(category.getName());
        dbCategory.setDescription(category.getDescription());

        return categoryRepository.save(dbCategory);
    }

    @Transactional
    public void delete(UUID id) {
        Category dbCategory = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category not found"));
        categoryRepository.deleteById(dbCategory.getId());
    }

}
