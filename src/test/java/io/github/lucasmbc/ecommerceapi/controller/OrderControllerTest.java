package io.github.lucasmbc.ecommerceapi.controller;

import io.github.lucasmbc.ecommerceapi.domain.model.*;
import io.github.lucasmbc.ecommerceapi.service.OrderService;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    private Customer customer;

    @BeforeEach
    public void setup() {
        customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setName("Joao");
        customer.setEmail("joao@email.com");
        customer.setPassword("123456");
        customer.setCpf("892.334.220-38");
    }

    @Test
    @DisplayName("POST /orders/{customerId} should return 201 Created with order details")
    void createOrder_ShouldReturn201Created_WhenCheckoutSuccessful() throws  Exception {

        Order order = createMockOrder(customer.getId());

        given(orderService.checkout(customer.getId())).willReturn(order);

        ResultActions response = mockMvc.perform(
                post("/orders/{customerId}", customer.getId()).contentType(MediaType.APPLICATION_JSON)
        );

        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location",
                        containsString("/orders/" + customer.getId() + "/" + order.getId().toString())))
                .andExpect(jsonPath("$.id", is(order.getId().toString())))
                .andExpect(jsonPath("$.status", is(order.getStatus().toString())))
                .andExpect(jsonPath("$.total", is(order.getTotal().doubleValue())))
                .andExpect(jsonPath("$.items", hasSize(order.getItems().size())));
    }

    @Test
    @DisplayName("POST /orders/{customerId} should return 404 when customer not found")
    void createOrder_ShouldReturn404_WhenCustomerNotFound()  throws  Exception {
        given(orderService.checkout(customer.getId())).willThrow(new NotFoundException("Cart not found"));

        ResultActions response = mockMvc.perform(
                post("/orders/{customerId}", customer.getId()).contentType(MediaType.APPLICATION_JSON)
        );

        response.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Cart not found")));
    }

    private Order createMockOrder(UUID customerId) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setCustomer(customer);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;

        var product1 = createProduct("Product 1", "Product 1 description", BigDecimal.valueOf(50.00), 10);

        var product2 = createProduct("Product 2", "Product 2 description", BigDecimal.valueOf(50.00), 10);

        List<OrderItem> items = Arrays.asList(
                createOrderItem(product1, product1.getPrice(), 1),
                createOrderItem(product2, product2.getPrice(), 1)
        );

        for (OrderItem orderItem : items) {
            total = total.add(orderItem.getUnitPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        }

        order.setItems(items);
        order.setTotal(total);

        return order;
    }

    private OrderItem createOrderItem(Product product, BigDecimal price, Integer quantity) {
        OrderItem item = new OrderItem();
        item.setId(UUID.randomUUID());
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setUnitPrice(price);
        return item;
    }

    private Product createProduct(String name, String description, BigDecimal price, Integer stock) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        return product;
    }
}