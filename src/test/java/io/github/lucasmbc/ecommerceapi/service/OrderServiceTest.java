package io.github.lucasmbc.ecommerceapi.service;

import io.github.lucasmbc.ecommerceapi.domain.model.*;
import io.github.lucasmbc.ecommerceapi.domain.repository.CartRepository;
import io.github.lucasmbc.ecommerceapi.domain.repository.OrderRepository;
import io.github.lucasmbc.ecommerceapi.service.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private OrderService orderService;

    private String userEmail;
    private Cart cart;
    private Customer customer;

    @BeforeEach
    void setup() {
        userEmail = "johnDoe@email.com";
        
        User user = new User();
        user.setEmail(userEmail);

        customer = new Customer();
        customer.setName("John Doe");
        customer.setUser(user);

        Product product = new Product();
        product.setName("iPhone");
        product.setPrice(BigDecimal.valueOf(100.0));
        product.setStock(10);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setUnitPrice(BigDecimal.valueOf(100.0));

        cart = new Cart();
        cart.setCustomer(customer);
        cart.setItems(List.of(cartItem));
    }

    @Test
    @DisplayName("Should create order from cart successfully")
    void checkout_ShouldCreateOrderFromCart_WhenCartExists() {
        // Given
        given(cartRepository.findByCustomerUserEmail(userEmail)).willReturn(Optional.of(cart));
        given(orderRepository.save(any(Order.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        Order order = orderService.checkout(userEmail);

        // Then
        assertNotNull(order);
        assertEquals(customer, order.getCustomer());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(0, BigDecimal.valueOf(200.0).compareTo(order.getTotal()));
        assertEquals(1, order.getItems().size());
        assertEquals(cart.getItems().getFirst().getProduct(), order.getItems().getFirst().getProduct());
        
        then(cartRepository).should().findByCustomerUserEmail(userEmail);
        then(orderRepository).should().save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw NotFoundException when cart is not found")
    void checkout_ShouldThrowNotFoundException_WhenCartDoesNotExist() {
        // Given
        given(cartRepository.findByCustomerUserEmail(userEmail)).willReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, () -> orderService.checkout(userEmail));
        assertEquals("Cart not found", exception.getMessage());
        
        then(cartRepository).should().findByCustomerUserEmail(userEmail);
        then(orderRepository).should(never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should calculate total as zero when cart is empty")
    void checkout_ShouldCalculateZeroTotal_WhenCartIsEmpty() {
        // Given
        cart.setItems(Collections.emptyList());
        given(cartRepository.findByCustomerUserEmail(userEmail)).willReturn(Optional.of(cart));
        given(orderRepository.save(any(Order.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        Order order = orderService.checkout(userEmail);

        // Then
        assertEquals(0, BigDecimal.ZERO.compareTo(order.getTotal()));
        assertTrue(order.getItems().isEmpty());
    }
}
