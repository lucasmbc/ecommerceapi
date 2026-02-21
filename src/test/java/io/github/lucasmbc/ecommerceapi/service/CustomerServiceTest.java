package io.github.lucasmbc.ecommerceapi.service;

import io.github.lucasmbc.ecommerceapi.domain.model.Customer;
import io.github.lucasmbc.ecommerceapi.domain.repository.CustomerRepository;
import io.github.lucasmbc.ecommerceapi.service.exception.BusinessException;
import io.github.lucasmbc.ecommerceapi.service.exception.EmailAlreadyExistsException;
import io.github.lucasmbc.ecommerceapi.service.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;

    @BeforeEach
    public void setup() {
        customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setName("teste");
        customer.setEmail("teste@email.com");
        customer.setPassword("123");
        customer.setCpf("12345678910");
    }

    @DisplayName("JUnit test should create customer")
    @Test
    void shouldCreateCustomer() {
        given(customerRepository.save(customer)).willReturn(customer);

        Customer saved =  customerService.create(customer);

        assertNotNull(saved);
        assertEquals(customer.getName(), saved.getName());
        assertEquals(customer.getEmail(), saved.getEmail());
        assertEquals(customer.getCpf(), saved.getCpf());
    }

    @DisplayName("JUnit test should throw exception when email exists")
    @Test
    void shouldThrowExceptionWhenEmailExists() {
        given(customerRepository.save(customer)).willThrow(new EmailAlreadyExistsException(customer.getEmail()));

        EmailAlreadyExistsException businessException = assertThrows(EmailAlreadyExistsException.class, () -> customerService.create(customer));

        assertEquals("Email already registered: " + customer.getEmail(), businessException.getMessage());
    }

    @DisplayName("JUnit test should find customer by id")
    @Test
    void shouldFindCustomerById() {
        given(customerRepository.findById(customer.getId())).willReturn(Optional.of(customer));

        Customer saved = customerService.findById(customer.getId());

        assertNotNull(saved);
        assertEquals("teste",  saved.getName());
    }

    @DisplayName("JUnit test should throw exception when customer not found")
    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {
        given(customerRepository.findById(customer.getId())).willThrow(new NotFoundException("Customer not found"));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> customerService.findById(customer.getId()));

        assertEquals("Customer not found", exception.getMessage());
    }

    @DisplayName("JUnit test should find all customers")
    @Test
    void shouldFindAllCustomers() {
        Customer customer1 = new Customer();
        customer1.setName("teste1");
        customer1.setEmail("teste1@email.com");
        customer1.setPassword("1234");
        customer1.setCpf("32145678910");

        given(customerRepository.findAll()).willReturn(List.of(customer, customer1));

        List<Customer> customers = customerService.findAll();

        assertNotNull(customers);
        assertEquals(2, customers.size());
    }

    @DisplayName("JUnit test should throw exception when customer list not found")
    @Test
    void shouldThrowExceptionWhenCustomerListNotFound() {
        given(customerRepository.findAll()).willThrow(new NotFoundException("Customers not found"));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> customerService.findAll());

        assertEquals("Customers not found", exception.getMessage());
    }

    @Test
    @DisplayName("JUnit test should update customer")
    void shouldUpdateCustomer() {
        given(customerRepository.findById(customer.getId())).willReturn(Optional.of(customer));

        customer.setName("updatedTest");
        customer.setPhone("85999251315");
        customer.setAddress("Rua teste");

        given(customerRepository.save(customer)).willReturn(customer);

        Customer updatedCustomer = customerService.update(customer.getId(), customer);

        assertEquals("updatedTest", updatedCustomer.getName());
        assertEquals("85999251315", updatedCustomer.getPhone());
        assertEquals("Rua teste", updatedCustomer.getAddress());
    }

    @Test
    @DisplayName("JUnit test should throw NotFoundException when updating a non-existent customer")
    void shouldThrowNotFoundExceptionWhenUpdatingNonExistentCustomer() {
        given(customerRepository.findById(customer.getId())).willThrow(new NotFoundException("Customer not found"));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> customerService.update(customer.getId(), customer));

        assertEquals("Customer not found", exception.getMessage());
    }

    @Test
    @DisplayName("JUnit test should delete customer")
    void shouldDeleteCustomer() {
        given(customerRepository.findById(customer.getId())).willReturn(Optional.of(customer));
        willDoNothing().given(customerRepository).deleteById(customer.getId());

        customerService.delete(customer.getId());

        verify(customerRepository, times(1)).deleteById(customer.getId());
    }

    @DisplayName("JUnit test should throw NotFoundException when deleting a non-existent customer")
    @Test
    void shouldThrowNotFoundExceptionWhenDeletingNonExistentCustomer() {
        given(customerRepository.findById(customer.getId())).willThrow(new NotFoundException("Customer not found"));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> customerService.delete(customer.getId()));

        assertEquals("Customer not found", exception.getMessage());
    }

}