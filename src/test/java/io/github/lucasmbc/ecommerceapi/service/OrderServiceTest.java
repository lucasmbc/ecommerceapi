package io.github.lucasmbc.ecommerceapi.service;

import io.github.lucasmbc.ecommerceapi.domain.model.*;
import io.github.lucasmbc.ecommerceapi.domain.repository.CartRepository;
import io.github.lucasmbc.ecommerceapi.domain.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private OrderService orderService;

    @DisplayName("JUnit test should create order from cart")
    @Test
    void shouldCreateOrderFromCart() {
        Customer customer = new Customer();
        customer.setName("John Doe");
        customer.setEmail("johndoe@email.com");
        customer.setPassword("johndoe");
        customer.setCpf("1234567890");

        Product product = new Product();
        product.setName("iPhone");
        product.setPrice(BigDecimal.TEN);
        product.setStock(10);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setUnitPrice(BigDecimal.TEN);

        Cart cart = new Cart();
        cart.setItems(List.of(cartItem));

        given(cartRepository.findByCustomerId(customer.getId())).willReturn(Optional.of(cart));
        given(orderRepository.save(any(Order.class))).willAnswer(invocation -> invocation.getArgument(0));

        Order order = orderService.checkout(customer.getId());

        assertEquals(order.getTotal(), BigDecimal.valueOf(20));
    }

}