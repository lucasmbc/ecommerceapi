package io.github.lucasmbc.ecommerceapi.controller;

import io.github.lucasmbc.ecommerceapi.controller.dto.request.ProductRequestDTO;
import io.github.lucasmbc.ecommerceapi.domain.model.Category;
import io.github.lucasmbc.ecommerceapi.domain.model.Product;
import io.github.lucasmbc.ecommerceapi.service.ProductService;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private Product savedProduct;
    private Category category;
    private ProductRequestDTO dto;

    @BeforeEach
    public void setup() {
        category = createCategory("Electronics");

        dto = createProductRequestDTO("iPhone", "iPhone description", BigDecimal.valueOf(3000), "https://placeimg.com/640/480", 10, category.getId());

        savedProduct = createProduct(dto.getName(), dto.getDescription(), dto.getPrice(), dto.getImageUrl(), dto.getStock(), category);
    }

    @Test
    @DisplayName("POST /products should create product and return 201 created product")
    void createProduct_ShouldReturnCreatedProduct() throws Exception {
        given(productService.create(any(ProductRequestDTO.class))).willReturn(savedProduct);

        ResultActions response = mockMvc.perform(
                post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
        );

        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id", is(savedProduct.getId().toString())))
                .andExpect(jsonPath("$.name", is(savedProduct.getName())))
                .andExpect(jsonPath("$.description", is(savedProduct.getDescription())))
                .andExpect(jsonPath("$.price", is(3000)))
                .andExpect(jsonPath("$.stock", is(savedProduct.getStock())))
                .andExpect(jsonPath("$.categoryId", is(category.getId().toString())))
                .andExpect(jsonPath("$.categoryName", is(category.getName())));
    }

    @Test
    @DisplayName("POST /products should return 400 when required fields are missing")
    void createProduct_ShouldReturnBadRequest_WhenRequiredFieldsMissing() throws Exception {

        String invalidJson = """
        {
            "name": "",
            "price": 100,
            "stock": 10,
            "categoryId": "123e4567-e89b-12d3-a456-426655440000"
        }
        """;

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.error", is("Validation error")));

        verify(productService, never()).create(any());
    }

    @Test
    @DisplayName("POST /products should return 400 when image URL is invalid")
    void createProduct_ShouldReturnBadRequest_WhenImageUrlIsInvalid() throws Exception {

        String invalidJson = """
        {
            "name": "IPhone",
            "price": 5000,
            "stock": 10,
            "imageUrl": "urlInvalid",
            "categoryId": "123e4567-e89b-12d3-a456-426655440000"
        }
        """;

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.error", is("Validation error")));

        verify(productService, never()).create(any());
    }

    @Test
    @DisplayName("POST /products should return 404 when category not found")
    void createProduct_ShouldReturnNotFound_WhenCategoryNotFound() throws Exception {
        given(productService.create(any(ProductRequestDTO.class)))
                .willThrow(new NotFoundException("Category not found"));

        ResultActions response = mockMvc.perform(
                post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
        );

        response.andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(jsonPath("$.message", containsString("Category not found")));
    }

    @Test
    @DisplayName("GET /products should return 200 OK and product list")
    void getAllProducts_ShouldReturnProductList() throws Exception {
        List<Product> productList = new ArrayList<>();

        Product product2 = createProduct("MacBook", "MacBook description", BigDecimal.valueOf(6500), "https://placeimg.com/640/480", 20, category);

        productList.add(savedProduct);
        productList.add(product2);

        given(productService.findAll()).willReturn(productList);

        ResultActions response = mockMvc.perform(get("/products"));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(productList.size())));
    }

    @Test
    @DisplayName("GET /products should return 404 NotFoundException when product is empty")
    void getAllProducts_ShouldReturnNotFoundException_WhenEmpty() throws Exception {
        given(productService.findAll()).willThrow(new NotFoundException("Products not found"));

        ResultActions response = mockMvc.perform(get("/products"));

        response.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Products not found")));
    }

    @Test
    @DisplayName("GET /products/{id} should return product when valid ID is provided")
    void getProductById_ShouldReturnProduct_WhenValidIdProvided() throws Exception {
        given(productService.findById(any(UUID.class))).willReturn(savedProduct);

        ResultActions response = mockMvc.perform(get("/products/{id}", savedProduct.getId()));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(savedProduct.getName())))
                .andExpect(jsonPath("$.description", is(savedProduct.getDescription())))
                .andExpect(jsonPath("$.price", is(savedProduct.getPrice().intValue())))
                .andExpect(jsonPath("$.stock", is(savedProduct.getStock())))
                .andExpect(jsonPath("$.categoryId", is(category.getId().toString())))
                .andExpect(jsonPath("$.categoryName", is(category.getName())));
    }

    @Test
    @DisplayName("GET /products/{id} should return 404 NotFoundException when invalid ID is provided")
    void getProductById_ShouldReturnNotFoundException_WhenInvalidIdProvided() throws Exception {
        given(productService.findById(any(UUID.class))).willThrow(new NotFoundException("Product not found"));

        ResultActions response = mockMvc.perform(get("/products/{id}", savedProduct.getId()));

        response.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Product not found")));
    }

    @Test
    @DisplayName("PUT /products/{id} should return 200 OK with updated product object")
    void updateProduct_ShouldReturnUpdatedProduct() throws Exception {
        var updatedProductDto = createProductRequestDTO("iPhone 14", "iPhone 14 description", BigDecimal.valueOf(3500), "https://placeimg.com/640/480", 10, category.getId());

        var savedUpdatedProduct = createProduct(updatedProductDto.getName(), updatedProductDto.getDescription(), updatedProductDto.getPrice(), updatedProductDto.getImageUrl(), updatedProductDto.getStock(), category);

        given(productService.update(any(UUID.class), any(ProductRequestDTO.class))).willReturn(savedUpdatedProduct);

        ResultActions response = mockMvc.perform(
                put("/products/{id}", savedProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProductDto))
        );

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(updatedProductDto.getName())))
                .andExpect(jsonPath("$.description", is(updatedProductDto.getDescription())))
                .andExpect(jsonPath("$.price", is(updatedProductDto.getPrice().intValue())))
                .andExpect(jsonPath("$.stock", is(updatedProductDto.getStock())));
    }

    @Test
    @DisplayName("PUT /products/{id} should return 404 NotFoundException when invalid product ID is provided")
    void updateProduct_ShouldReturnNotFoundException_WhenInvalidProductIdProvided() throws Exception {
        var updatedProductDto = createProductRequestDTO("iPhone 14", "iPhone 14 description", BigDecimal.valueOf(3500), "https://placeimg.com/640/480", 10, category.getId());

        given(productService.update(any(UUID.class), any(ProductRequestDTO.class))).willThrow(new NotFoundException("Product not found"));

        ResultActions response = mockMvc.perform(
                put("/products/{id}", savedProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProductDto))
        );

        response.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Product not found")));
    }

    @Test
    @DisplayName("PUT /products/{id} should return 404 NotFoundException when invalid category ID is provided")
    void updateProduct_ShouldReturnNotFoundException_WhenInvalidCategoryIdProvided() throws Exception {
        var updatedProductDto = createProductRequestDTO("iPhone 14", "iPhone 14 description", BigDecimal.valueOf(3500), "https://placeimg.com/640/480", 10, category.getId());

        given(productService.update(any(UUID.class), any(ProductRequestDTO.class))).willThrow(new NotFoundException("Category not found"));

        ResultActions response = mockMvc.perform(
                put("/products/{id}", savedProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProductDto))
        );

        response.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Category not found")));
    }

    @Test
    @DisplayName("DELETE /products/{id} should return 204 No Content when product exists")
    void deleteProduct_ShouldReturnNoContent_WhenProductExists() throws Exception {
        willDoNothing().given(productService).delete(any(UUID.class));

        ResultActions response = mockMvc.perform(delete("/products/{id}", savedProduct.getId()));

        response.andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /products/{id} should return 404 NotFoundException when product does not exists")
    void deleteProduct_ShouldReturnNotFoundException_WhenProductDoesNotExist() throws Exception {
        willThrow(new NotFoundException("Product not found")).given(productService).delete(any(UUID.class));

        ResultActions response = mockMvc.perform(delete("/products/{id}", savedProduct.getId()));

        response.andDo(print())
                .andExpect(status().isNotFound());
    }

    private Product createProduct(String name, String description, BigDecimal price, String imageURl, Integer stock, Category category) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setImageUrl(imageURl);
        product.setCategory(category);
        return product;
    }

    private Category createCategory(String name) {
        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setName(name);
        return category;
    }

    private ProductRequestDTO createProductRequestDTO(String name, String description, BigDecimal price, String imageUrl, Integer stock, UUID categoryId) {
        ProductRequestDTO productRequestDTO = new ProductRequestDTO();
        productRequestDTO.setName(name);
        productRequestDTO.setDescription(description);
        productRequestDTO.setPrice(price);
        productRequestDTO.setStock(stock);
        productRequestDTO.setImageUrl(imageUrl);
        productRequestDTO.setCategoryId(categoryId);
        return productRequestDTO;
    }
}