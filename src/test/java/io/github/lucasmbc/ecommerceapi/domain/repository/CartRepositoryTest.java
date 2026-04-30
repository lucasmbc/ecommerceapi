package io.github.lucasmbc.ecommerceapi.domain.repository;

import io.github.lucasmbc.ecommerceapi.domain.model.Cart;
import io.github.lucasmbc.ecommerceapi.domain.model.Customer;
import io.github.lucasmbc.ecommerceapi.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer customer;

    @DisplayName("JUnit test should find cart by customerId")
    @Test
    void shouldFindCartByCustomerUserEmail() {
        User user = new User();
        user.setEmail("user@email.com");
        user.setPassword("123456");
        user.setRole("USER");
        user.setCustomer(customer);

        customer = new Customer();
        customer.setName("teste");
        customer.setCpf("12345678910");
        customer.setPhone("(85)999999999");
        customer.setAddress("Rua das Flores");
        customer.setUser(user);

        userRepository.save(user);
        customerRepository.save(customer);

        Cart cart = new Cart();
        cart.setCustomer(customer);
        cart.setCreatedAt(LocalDateTime.now());

        cartRepository.save(cart);

        var result = cartRepository.findByCustomerUserEmail(customer.getUser().getEmail());

        assertNotNull(result);
        assertEquals(customer.getName(), result.get().getCustomer().getName());
    }
}