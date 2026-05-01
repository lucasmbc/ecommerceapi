package io.github.lucasmbc.ecommerceapi.controller;

import io.github.lucasmbc.ecommerceapi.domain.model.Order;
import io.github.lucasmbc.ecommerceapi.domain.model.OrderStatus;
import io.github.lucasmbc.ecommerceapi.service.JwtService;
import io.github.lucasmbc.ecommerceapi.service.OrderService;
import io.github.lucasmbc.ecommerceapi.service.exception.BusinessException;
import io.github.lucasmbc.ecommerceapi.service.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private Order mockOrder;
    private final String userEmail = "test@example.com";
    private UsernamePasswordAuthenticationToken auth;

    @BeforeEach
    public void setup() {
        mockOrder = new Order();
        mockOrder.setId(UUID.randomUUID());
        mockOrder.setOrderDate(LocalDateTime.now());
        mockOrder.setStatus(OrderStatus.PENDING);
        mockOrder.setTotal(BigDecimal.valueOf(150.00));
        mockOrder.setItems(List.of());

        auth = new UsernamePasswordAuthenticationToken(userEmail, null, Collections.emptyList());
    }

    @Test
    @DisplayName("POST /orders should return 201 Created with order details")
    void createOrder_ShouldReturn201Created_WhenCheckoutSuccessful() throws Exception {
        given(orderService.checkout(userEmail)).willReturn(mockOrder);

        ResultActions response = mockMvc.perform(
                post("/orders")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", containsString("/orders/" + mockOrder.getId())))
                .andExpect(jsonPath("$.id", is(mockOrder.getId().toString())))
                .andExpect(jsonPath("$.status", is(mockOrder.getStatus().toString())))
                .andExpect(jsonPath("$.total").value(150.0))
                .andExpect(jsonPath("$.orderDate").exists());

        then(orderService).should(times(1)).checkout(userEmail);
    }

    @Test
    @DisplayName("POST /orders should return 404 Not Found when customer or cart not found")
    void createOrder_ShouldReturn404_WhenServiceThrowsNotFoundException() throws Exception {
        given(orderService.checkout(userEmail)).willThrow(new NotFoundException("Cart not found for user: " + userEmail));

        mockMvc.perform(
                        post("/orders")
                                .principal(auth)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Resource not found")))
                .andExpect(jsonPath("$.message", containsString("Cart not found")));

        then(orderService).should(times(1)).checkout(userEmail);
    }

    @Test
    @DisplayName("POST /orders should return 422 Unprocessable Entity when business rule fails")
    void createOrder_ShouldReturn422_WhenServiceThrowsBusinessException() throws Exception {
        given(orderService.checkout(userEmail)).willThrow(new BusinessException("Insufficient stock"));

        mockMvc.perform(
                        post("/orders")
                                .principal(auth)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.error", is("Business error")))
                .andExpect(jsonPath("$.message", is("Insufficient stock")));

        then(orderService).should(times(1)).checkout(userEmail);
    }
}
