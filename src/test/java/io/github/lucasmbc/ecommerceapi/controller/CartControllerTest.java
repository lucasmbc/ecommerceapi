package io.github.lucasmbc.ecommerceapi.controller;

import io.github.lucasmbc.ecommerceapi.domain.model.CartItem;
import io.github.lucasmbc.ecommerceapi.domain.model.Customer;
import io.github.lucasmbc.ecommerceapi.domain.model.Product;
import io.github.lucasmbc.ecommerceapi.service.CartService;
import io.github.lucasmbc.ecommerceapi.service.exception.CustomBadRequestException;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CartService cartService;

    private Customer customer;

    private Product product;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setName("Joao");
        customer.setEmail("joao@email.com");
        customer.setPassword("123456");
        customer.setCpf("892.334.220-38");

        product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("iPhone");
        product.setDescription("iPhone description");
        product.setPrice(BigDecimal.valueOf(3000));
        product.setStock(10);
    }

    @Test
    @DisplayName("POST /{customerId}/items/{productId} should create cart and return 201 created cart")
    void createCart_ShouldReturnCreatedCart() throws Exception {
        doNothing().when(cartService).addItem(any(UUID.class), any(UUID.class), anyInt());

        ResultActions response = mockMvc.perform(
                post("/carts/{customerId}/items/{productId}", customer.getId(), product.getId())
                        .param("quantity", "1")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location",
                        containsString("/carts/" + customer.getId() + "/items/" + product.getId())));

        verify(cartService, times(1))
                .addItem(customer.getId(), product.getId(), 1);
    }

    @Test
    @DisplayName("Should return 400 when quantity is zero")
    void addItem_ShouldReturnBadRequest_WhenQuantityIsZero() throws Exception {

        doThrow(new CustomBadRequestException("Quantity must be greater than zero")).when(cartService).addItem(any(UUID.class), any(UUID.class), anyInt());

        ResultActions response = mockMvc.perform(post("/carts/{customerId}/items/{productId}", customer.getId(), product.getId())
                .param("quantity", "0")
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Quantity must be greater than zero")));
    }

    @Test
    @DisplayName("Should return 400 when quantity is negative")
    void addItem_ShouldReturnBadRequest_WhenQuantityIsNegative() throws Exception {

        doThrow(new CustomBadRequestException("Quantity must be greater than zero")).when(cartService).addItem(any(UUID.class), any(UUID.class), anyInt());

        ResultActions response = mockMvc.perform(post("/carts/{customerId}/items/{productId}", customer.getId(), product.getId())
                .param("quantity", "-1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Quantity must be greater than zero")));
    }

    @Test
    @DisplayName("Should return 400 when quantity parameter is missing")
    void addItem_ShouldReturnBadRequest_WhenQuantityIsMissing() throws Exception {

        doThrow(new CustomBadRequestException("Quantity must be greater than zero")).when(cartService).addItem(any(UUID.class), any(UUID.class), anyInt());

        ResultActions response = mockMvc.perform(post("/carts/{customerId}/items/{productId}", customer.getId(), product.getId())
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when quantity exceeds product stock")
    void addItem_ShouldReturnBadRequest_WhenQuantityExceedsProductStock() throws Exception {

        doThrow(new CustomBadRequestException("Product stock less than quantity")).when(cartService).addItem(any(UUID.class), any(UUID.class), anyInt());

        ResultActions response = mockMvc.perform(post("/carts/{customerId}/items/{productId}", customer.getId(), product.getId())
                .param("quantity", "11")
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Product stock less than quantity")));
    }

    @Test
    @DisplayName("Should return 404 when customer not found")
    void addItem_ShouldReturnNotFound_WhenCustomerNotFound() throws Exception {
        UUID customerId = UUID.randomUUID();

        doThrow(new NotFoundException("Customer not found")).when(cartService).addItem(any(UUID.class), any(UUID.class), anyInt());

        ResultActions response = mockMvc.perform(post("/carts/{customerId}/items/{productId}", customerId, product.getId())
                .param("quantity", "1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Customer not found")));
    }

    @Test
    @DisplayName("Should return 404 when product not found")
    void addItem_ShouldReturnNotFound_WhenProductNotFound() throws Exception {
        UUID productId = UUID.randomUUID();

        doThrow(new NotFoundException("Product not found")).when(cartService).addItem(any(UUID.class), any(UUID.class), anyInt());

        ResultActions response = mockMvc.perform(post("/carts/{customerId}/items/{productId}", customer.getId(), productId)
                .param("quantity", "1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Product not found")));
    }

    @Test
    @DisplayName("GET /carts/{customerId} should return cart items when valid ID is provided")
    void getCartById_ShouldReturnCartItems_WhenValidIdProvided() throws Exception {
        List<CartItem> cartItems = Arrays.asList(
                createCartItem("iPhone", BigDecimal.valueOf(3000), 2),
                createCartItem("MacBook", BigDecimal.valueOf(8000), 1)
        );

        given(cartService.getItems(any(UUID.class))).willReturn(cartItems);

        ResultActions response = mockMvc.perform(
                get("/carts/{customerId}", customer.getId())
        );

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].productName", is("iPhone")))
                .andExpect(jsonPath("$[0].quantity", is(2)))
                .andExpect(jsonPath("$[1].productName", is("MacBook")))
                .andExpect(jsonPath("$[1].quantity", is(1)));
    }

    @Test
    @DisplayName("GET /carts/{customerId} should return 404 when cart not found")
    void getCartById_ShouldReturn404_WhenCartNotFound() throws Exception {

        given(cartService.getItems(customer.getId()))
                .willThrow(new NotFoundException("Cart not found"));

        ResultActions response = mockMvc.perform(
                get("/carts/{customerId}", customer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        );

        response.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Cart not found")));

    }

    private CartItem createCartItem(String productName, BigDecimal price, Integer quantity) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(productName);
        product.setPrice(price);

        CartItem cartItem = new CartItem();
        cartItem.setId(UUID.randomUUID());
        cartItem.setUnitPrice(product.getPrice());
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);

        return cartItem;
    }
}