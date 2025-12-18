package io.github.lucasmbc.ecommerceapi.domain.repository;

import io.github.lucasmbc.ecommerceapi.domain.model.Customer;
import io.github.lucasmbc.ecommerceapi.domain.model.Order;
import io.github.lucasmbc.ecommerceapi.domain.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setName("teste");
        customer.setEmail("teste@cart.com");
        customer.setPassword("123");
        customer.setCpf("12345678910");

        customerRepository.save(customer);

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setTotal(BigDecimal.valueOf(100));

        orderRepository.save(order);
    }

    @DisplayName("JUnit test should find order by customerId")
    @Test
    void shouldFindOrdersByCustomerId() {

        List<Order> orders = orderRepository.findByCustomerId(customer.getId());

        assertNotNull(orders);
        assertEquals(1, orders.size());
    }

    @DisplayName("JUnit test should find orders by status")
    @Test
    void shouldFindOrdersByStatus() {

        List<Order> orders = orderRepository.findByStatus(OrderStatus.PENDING);

        assertFalse(orders.isEmpty());
    }
}