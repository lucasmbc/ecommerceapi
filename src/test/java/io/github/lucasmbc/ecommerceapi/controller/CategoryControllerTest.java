package io.github.lucasmbc.ecommerceapi.controller;

import io.github.lucasmbc.ecommerceapi.controller.dto.request.CategoryRequestDTO;
import io.github.lucasmbc.ecommerceapi.domain.model.Category;
import io.github.lucasmbc.ecommerceapi.service.CategoryService;
import io.github.lucasmbc.ecommerceapi.service.exception.BusinessException;
import io.github.lucasmbc.ecommerceapi.service.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    private CategoryRequestDTO dto;
    private Category category;

    @BeforeEach
    void setUp() {
        dto = new CategoryRequestDTO();
        dto.setName("Category 1");
        dto.setDescription("Category 1 description");

        category = new Category();
        category.setId(UUID.randomUUID());
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
    }

    @Test
    @DisplayName("POST /categories should create category and return 201 created category")
    void createCategory_ShouldReturnCreatedCategory() throws Exception {

        given(categoryService.create(any(Category.class))).willReturn(category);

        ResultActions response = mockMvc.perform(
                post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
        );

        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id", is(category.getId().toString())))
                .andExpect(jsonPath("$.name", is(category.getName())))
                .andExpect(jsonPath("$.description", is(category.getDescription())));
    }

    @Test
    @DisplayName("POST /categories should return 400 when required fields are missing")
    void createCategory_ShouldReturnBadRequest_WhenRequiredFieldsMissing() throws Exception {

        String invalidJson = """
        {
            "name": "",
            "description": "Category 1 description"
        }
        """;

        ResultActions response = mockMvc.perform(
                post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson)
        );

        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation error")));
    }

    @Test
    @DisplayName("POST /categories should return 422 when category already exists")
    void createCategory_ShouldReturnUnprocessableContent_WhenCategoryAlreadyExists() throws Exception {

        given(categoryService.create(any(Category.class))).willThrow(new BusinessException("Category with name " + category.getName() + " already exists"));

        ResultActions response = mockMvc.perform(
                post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
        );

        response.andDo(print())
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.message", containsString("Category with name " + category.getName() + " already exists")));
    }

    @Test
    @DisplayName("GET /categories should return 200 OK and category list")
    void findAllCategory_ShouldReturnCategoryList() throws Exception {
        List<Category> categoryList = new ArrayList<>();

        Category newCategory = new Category();
        newCategory.setId(UUID.randomUUID());
        newCategory.setName("Category 2");
        newCategory.setDescription("Category 2 description");

        categoryList.add(category);
        categoryList.add(newCategory);

        given(categoryService.findAll()).willReturn(categoryList);

        ResultActions response = mockMvc.perform(get("/categories"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(categoryList.size())));
    }

    @Test
    @DisplayName("GET /categories should return 404 NotFoundException when category is empty")
    void findAllCategory_ShouldReturnNotFoundException_WhenEmpty() throws Exception {

        given(categoryService.findAll()).willThrow(new NotFoundException("Categories not found"));

        ResultActions response = mockMvc.perform(get("/categories"));

        response.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Categories not found")));
    }

    @Test
    @DisplayName("GET /categories/{id} should return category when valid ID is provided")
    void getCategoryById_ShouldReturnCategory_WhenValidIdProvided() throws Exception {
        given(categoryService.findById(any(UUID.class))).willReturn(category);

        ResultActions response = mockMvc.perform(get("/categories/{id}", category.getId()));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(category.getName())))
                .andExpect(jsonPath("$.description", is(category.getDescription())));
    }

    @Test
    @DisplayName("GET /categories/{id} should return 404 NotFoundException when invalid ID is provided")
    void getCategoryById_ShouldReturnNotFoundException_WhenInvalidIdProvided() throws Exception {
        given(categoryService.findById(any(UUID.class))).willThrow(new NotFoundException("Category not found"));

        ResultActions response = mockMvc.perform(get("/categories/{id}", category.getId()));

        response.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Category not found")));
    }

    @Test
    @DisplayName("PUT /categories/{id} should return 200 OK with updated category object")
    void updateCategory_ShouldReturnUpdatedCategory() throws Exception {
        CategoryRequestDTO categoryRequestDTO = new CategoryRequestDTO();
        categoryRequestDTO.setName("Books");
        categoryRequestDTO.setDescription("Books description");

        Category updatedCategory = new Category();
        updatedCategory.setId(UUID.randomUUID());
        updatedCategory.setName(categoryRequestDTO.getName());
        updatedCategory.setDescription(categoryRequestDTO.getDescription());

        given(categoryService.update(any(UUID.class), any(Category.class))).willReturn(updatedCategory);

        ResultActions response = mockMvc.perform(
                put("/categories/{id}", category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDTO))
        );

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(categoryRequestDTO.getName())))
                .andExpect(jsonPath("$.description", is(categoryRequestDTO.getDescription())));
    }

    @Test
    @DisplayName("PUT /categories/{id} should return 404 NotFoundException when invalid category ID is provided")
    void updateCategory_ShouldReturnNotFoundException_WhenInvalidCategoryIdProvided() throws Exception {
        CategoryRequestDTO categoryRequestDTO = new CategoryRequestDTO();
        categoryRequestDTO.setName("Books");
        categoryRequestDTO.setDescription("Books description");

        given(categoryService.update(any(UUID.class), any(Category.class))).willThrow(new NotFoundException("Category not found"));

        ResultActions response = mockMvc.perform(
                put("/categories/{id}", category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDTO))
        );

        response.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Category not found")));
    }

    @Test
    @DisplayName("DELETE /categories/{id} should return 204 No Content when category exists")
    void deleteCategory_ShouldReturnNoContent_WhenCategoryExists() throws Exception {
        willDoNothing().given(categoryService).delete(any(UUID.class));

        ResultActions response = mockMvc.perform(delete("/categories/{id}", category.getId()));

        response.andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /categories/{id} should return 404 NotFoundException when invalid category ID is provided")
    void deleteCategory_ShouldReturnNotFoundException_WhenInvalidCategoryIdProvided() throws Exception {
        willThrow(new NotFoundException("Category not found")).given(categoryService).delete(any(UUID.class));

        ResultActions response = mockMvc.perform(delete("/categories/{id}", category.getId()));

        response.andDo(print())
                .andExpect(status().isNotFound());
    }
}