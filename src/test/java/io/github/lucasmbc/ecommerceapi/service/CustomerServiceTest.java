package io.github.lucasmbc.ecommerceapi.service;

import io.github.lucasmbc.ecommerceapi.controller.dto.request.CustomerRequestDTO;
import io.github.lucasmbc.ecommerceapi.domain.model.Customer;
import io.github.lucasmbc.ecommerceapi.domain.model.User;
import io.github.lucasmbc.ecommerceapi.domain.repository.CustomerRepository;
import io.github.lucasmbc.ecommerceapi.domain.repository.UserRepository;
import io.github.lucasmbc.ecommerceapi.service.exception.EmailAlreadyExistsException;
import io.github.lucasmbc.ecommerceapi.service.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private CustomerRequestDTO dto;
    private User user;

    @BeforeEach
    public void setup() {
        user = createUser("teste@email.com", "1234", "ROLE_USER");

        dto = createCustomerRequestDTO("teste", "12345678910", "88999999999", "Rua das Oliveiras");

        customer = createCustomer(dto, user);
    }

    private User createUser(String email, String password, String role) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        return user;
    }

    private CustomerRequestDTO createCustomerRequestDTO(String name, String cpf, String phone, String address) {
        CustomerRequestDTO dto = new CustomerRequestDTO();
        dto.setName(name);
        dto.setCpf(cpf);
        dto.setPhone(phone);
        dto.setAddress(address);
        return dto;
    }

    private Customer createCustomer(CustomerRequestDTO dto, User user) {
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setName(dto.getName());
        customer.setCpf(dto.getCpf());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());
        customer.setUser(user);
        return customer;
    }

    @DisplayName("JUnit test should create customer")
    @Test
    void shouldCreateCustomer() {
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        given(customerRepository.save(any(Customer.class))).willReturn(customer);

        Customer saved =  customerService.create(dto, user.getEmail());

        assertNotNull(saved);
        assertEquals(customer.getName(), saved.getName());
        assertEquals(customer.getUser().getEmail(), saved.getUser().getEmail());
        assertEquals(customer.getCpf(), saved.getCpf());
        assertEquals(customer.getPhone(), saved.getPhone());
        assertEquals(customer.getAddress(), saved.getAddress());
    }

    @DisplayName("JUnit test should throw exception when email exists")
    @Test
    void shouldThrowExceptionWhenEmailExists() {
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        given(customerRepository.save(any(Customer.class))).willThrow(new EmailAlreadyExistsException(customer.getUser().getEmail()));

        EmailAlreadyExistsException businessException = assertThrows(EmailAlreadyExistsException.class, () -> customerService.create(dto, user.getEmail()));

        assertEquals("Email already registered: " + customer.getUser().getEmail(), businessException.getMessage());
    }

    @DisplayName("JUnit test should find customer by id")
    @Test
    void shouldFindCustomerByEmail() {
        given(customerRepository.findByUserEmail(customer.getUser().getEmail())).willReturn(Optional.of(customer));

        Customer saved = customerService.findByEmail(customer.getUser().getEmail());

        assertNotNull(saved);
        assertEquals("teste",  saved.getName());
    }

    @DisplayName("JUnit test should throw exception when customer not found")
    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {
        given(customerRepository.findByUserEmail(customer.getUser().getEmail())).willThrow(new NotFoundException("Customer not found"));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> customerService.findByEmail(customer.getUser().getEmail()));

        assertEquals("Customer not found", exception.getMessage());
    }

    @DisplayName("JUnit test should find all customers")
    @Test
    void shouldFindAllCustomers() {
        CustomerRequestDTO dto1 = createCustomerRequestDTO("teste1", "32145678910", "85999885577", "Rua das Andorinhas");
        Customer customer1 = createCustomer(dto1, user);

        var customerList = List.of(customer, customer1);
        var page = new PageImpl<>(customerList, PageRequest.of(0, 10), customerList.size());

        given(customerRepository.findAll(any(Pageable.class))).willReturn(page);

        var foundCustomersPage = customerService.findAll(PageRequest.of(0, 10));

        assertNotNull(foundCustomersPage);
        assertEquals(2, foundCustomersPage.getContent().size());
    }

    @DisplayName("JUnit test should throw exception when customer list not found")
    @Test
    void shouldThrowExceptionWhenCustomerListNotFound() {
        given(customerRepository.findAll(any(Pageable.class))).willThrow(new NotFoundException("Customers not found"));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> customerService.findAll(PageRequest.of(0, 10)));

        assertEquals("Customers not found", exception.getMessage());
    }

    @Test
    @DisplayName("JUnit test should update customer")
    void shouldUpdateCustomer() {
        given(userRepository.findByEmail(customer.getUser().getEmail())).willReturn(Optional.of(user));
        given(customerRepository.findByUserEmail(customer.getUser().getEmail())).willReturn(Optional.of(customer));

        CustomerRequestDTO updatedCustomerDTO = createCustomerRequestDTO("updatedTest", "12345678910", "85999251315", "Rua teste");

        customer = createCustomer(updatedCustomerDTO, user);

        given(customerRepository.save(any(Customer.class))).willReturn(customer);

        Customer updatedCustomer = customerService.update(customer.getUser().getEmail(), updatedCustomerDTO);

        assertEquals("updatedTest", updatedCustomer.getName());
        assertEquals("85999251315", updatedCustomer.getPhone());
        assertEquals("Rua teste", updatedCustomer.getAddress());
    }

    @Test
    @DisplayName("JUnit test should throw NotFoundException when updating a non-existent customer")
    void shouldThrowNotFoundExceptionWhenUpdatingNonExistentCustomer() {
        given(customerRepository.findByUserEmail(customer.getUser().getEmail())).willThrow(new NotFoundException("Customer not found"));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> customerService.update(customer.getUser().getEmail(), dto));

        assertEquals("Customer not found", exception.getMessage());
    }

    @Test
    @DisplayName("JUnit test should delete customer")
    void shouldDeleteCustomer() {
        given(customerRepository.findByUserEmail(customer.getUser().getEmail())).willReturn(Optional.of(customer));
        willDoNothing().given(customerRepository).deleteById(customer.getId());

        customerService.delete(customer.getUser().getEmail());

        then(customerRepository).should(times(1)).deleteById(customer.getId());
    }

    @DisplayName("JUnit test should throw NotFoundException when deleting a non-existent customer")
    @Test
    void shouldThrowNotFoundExceptionWhenDeletingNonExistentCustomer() {
        given(customerRepository.findByUserEmail(customer.getUser().getEmail())).willThrow(new NotFoundException("Customer not found"));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> customerService.delete(customer.getUser().getEmail()));

        assertEquals("Customer not found", exception.getMessage());
    }

}