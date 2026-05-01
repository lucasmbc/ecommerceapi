package io.github.lucasmbc.ecommerceapi.domain.repository;

import io.github.lucasmbc.ecommerceapi.domain.model.Customer;
import io.github.lucasmbc.ecommerceapi.domain.model.User;
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

    @Autowired
    private UserRepository userRepository;

    private Customer customer;
    private User user;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setEmail("user@email.com");
        user.setPassword("123456");
        user.setRole("USER");
        userRepository.save(user);

        customer = new Customer();
        customer.setName("teste");
        customer.setCpf("12345678910");
        customer.setPhone("(85)999999999");
        customer.setAddress("Rua das Flores");
        customer.setUser(user);
        customerRepository.save(customer);
    }

    @DisplayName("JUnit test should save and find customer by email")
    @Test
    void shouldSaveAndFindCustomerByUserEmail() {

        var result = customerRepository.findByUserEmail(customer.getUser().getEmail());

        assertNotNull(result);
        assertEquals("teste", result.get().getName());
    }

    @DisplayName("JUnit test should return true when email exists")
    @Test
    void shouldReturnTrueWhenCpfExists() {

        boolean exists = customerRepository.existsByCpf(customer.getCpf());

        assertTrue(exists);
    }
}
