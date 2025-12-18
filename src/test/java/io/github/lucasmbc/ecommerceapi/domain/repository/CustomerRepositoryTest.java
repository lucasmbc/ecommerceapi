package io.github.lucasmbc.ecommerceapi.domain.repository;

import io.github.lucasmbc.ecommerceapi.domain.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    private Customer customer;

    @BeforeEach
    public void setup() {
        customer = new Customer();
        customer.setName("teste");
        customer.setEmail("teste@email.com");
        customer.setPassword("123");
        customer.setCpf("12345678910");
    }

    @DisplayName("JUnit test should save and find customer by email")
    @Test
    void shouldSaveAndFindCustomerByEmail() {
        customerRepository.save(customer);

        var result = customerRepository.findByEmail("teste@email.com");

        assertNotNull(result);
        assertEquals("teste", result.get().getName());
    }

    @DisplayName("JUnit test should return true when email exists")
    @Test
    void shouldReturnTrueWhenEmailExists() {
        customerRepository.save(customer);

        boolean exists = customerRepository.existsByEmail("teste@email.com");

        assertTrue(exists);
    }
}
