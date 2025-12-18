package io.github.lucasmbc.ecommerceapi.domain.repository;

import io.github.lucasmbc.ecommerceapi.domain.model.Cart;
import io.github.lucasmbc.ecommerceapi.domain.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @DisplayName("JUnit test should find cart by customerId")
    @Test
    void shouldFindCartByCustomerId() {
        Customer customer = new Customer();
        customer.setName("teste");
        customer.setEmail("teste@cart.com");
        customer.setPassword("123");
        customer.setCpf("12345678910");
        customerRepository.save(customer);

        Cart cart = new Cart();
        cart.setCustomer(customer);
        cart.setCreatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        var result = cartRepository.findByCustomerId(customer.getId());

        assertNotNull(result);
        assertEquals(customer.getName(), result.get().getCustomer().getName());
    }
}