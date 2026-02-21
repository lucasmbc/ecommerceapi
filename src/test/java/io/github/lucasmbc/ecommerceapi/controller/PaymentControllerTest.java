package io.github.lucasmbc.ecommerceapi.controller;

import io.github.lucasmbc.ecommerceapi.domain.model.*;
import io.github.lucasmbc.ecommerceapi.service.PaymentService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = createMockPayment(PaymentType.PIX, PaymentStatus.APPROVED);
    }

    @Test
    @DisplayName("POST /payments/{orderId} should return 201 Created")
    void createPayment_ShouldReturn201Created_WhenPaySuccessful() throws Exception {

        given(paymentService.pay(payment.getOrder().getId(), payment.getPaymentType())).willReturn(payment);

        ResultActions response = mockMvc.perform(
                post("/payments/{orderId}", payment.getOrder().getId())
                        .param("paymentType", payment.getPaymentType().toString())
                        .contentType(MediaType.APPLICATION_JSON)
        );

        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location",
                        containsString("/payments/" + payment.getOrder().getId() + "/" + payment.getId().toString())))
                .andExpect(jsonPath("$.orderId", is(payment.getOrder().getId().toString())))
                .andExpect(jsonPath("$.status", is(payment.getStatus().toString())))
                .andExpect(jsonPath("$.paymentType", is(payment.getPaymentType().toString())))
                .andExpect(jsonPath("$.amount", is(payment.getAmount().doubleValue())));
    }

    @Test
    @DisplayName("POST /payments/{orderId} should return 404 when order not found")
    void createPayment_ShouldReturn404_WhenOrderNotFound() throws Exception {

        given(paymentService.pay(payment.getOrder().getId(), payment.getPaymentType())).willThrow(new NotFoundException("Order not found"));

        ResultActions response = mockMvc.perform(
                post("/payments/{orderId}", payment.getOrder().getId())
                        .param("paymentType", payment.getPaymentType().toString())
                        .contentType(MediaType.APPLICATION_JSON)
        );

        response.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Order not found")));
    }

    private Payment createMockPayment(PaymentType paymentType, PaymentStatus paymentStatus) {
        Order order = createOrder();
        Payment payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setPaymentType(paymentType);
        payment.setStatus(paymentStatus);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setAmount(order.getTotal());
        payment.setOrder(order);
        return payment;
    }

    private Order createOrder() {
        Customer customer = createCustomer();
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

    private Customer createCustomer() {
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setName("Joao");
        customer.setEmail("joao@email.com");
        customer.setPassword("123456");
        customer.setCpf("892.334.220-38");
        return customer;
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