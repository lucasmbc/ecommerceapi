package io.github.lucasmbc.ecommerceapi.controller;

import io.github.lucasmbc.ecommerceapi.controller.dto.request.CustomerRequestDTO;
import io.github.lucasmbc.ecommerceapi.domain.model.Customer;
import io.github.lucasmbc.ecommerceapi.domain.model.User;
import io.github.lucasmbc.ecommerceapi.security.JwtAuthFilter;
import io.github.lucasmbc.ecommerceapi.service.CustomerService;
import io.github.lucasmbc.ecommerceapi.service.exception.CpfAlreadyExistsException;
import io.github.lucasmbc.ecommerceapi.service.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
@AutoConfigureMockMvc(addFilters = false)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService customerService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    private CustomerRequestDTO dto;
    private Customer customer;
    private User user;
    private final String userEmail = "joao@email.com";
    private UsernamePasswordAuthenticationToken auth;

    @BeforeEach
    public void setup() {
        user = createUser(userEmail, "123456", "ROLE_USER");

        dto = createCustomerRequestDTO("Joao", "892.334.220-38", "(85)988128522", "Rua das Andorinhas");

        customer = createCustomer(dto, user);

        auth = new UsernamePasswordAuthenticationToken(userEmail, null, List.of());
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

    private String createJson(Map<String, Object> fields) throws Exception {
        return objectMapper.writeValueAsString(fields);
    }

    @Test
    @DisplayName("POST /customers should create customer and return 201 created customer")
    void createCustomer_ShouldReturnCreatedCustomer() throws Exception {
        given(customerService.create(any(CustomerRequestDTO.class), anyString())).willReturn(customer);

        ResultActions response = mockMvc.perform(
                post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(auth)
                        .content(objectMapper.writeValueAsString(dto))
        );

        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(customer.getName())))
                .andExpect(jsonPath("$.cpf", is(customer.getCpf())))
                .andExpect(jsonPath("$.phone", is(customer.getPhone())))
                .andExpect(jsonPath("$.address", is(customer.getAddress())));

        then(customerService).should().create(any(CustomerRequestDTO.class), eq(userEmail));
    }

    @Test
    @DisplayName("POST /customers should return 409 CpfAlreadyExistsException when cpf already exists")
    void createCustomer_ShouldReturnCpfAlreadyExistsException_WhenCpfAlreadyExists() throws Exception {
        given(customerService.create(any(CustomerRequestDTO.class), anyString())).willThrow(new CpfAlreadyExistsException(customer.getCpf()));

        ResultActions response = mockMvc.perform(
                post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(auth)
                        .content(objectMapper.writeValueAsString(customer))
        );

        response.andExpect(status().isConflict())
                .andDo(print())
                .andExpect(jsonPath("$.message", is("CPF already registered: " + customer.getCpf())));

        then(customerService).should(times(1)).create(any(CustomerRequestDTO.class), eq(userEmail));
    }

    @Test
    @DisplayName("POST /customers should return 400 when the name field is missing")
    void createCustomer_ShouldReturnBadRequest_WhenNameFieldMissing() throws Exception {

        String invalidJson = createJson(Map.of(
                "name", "",
                "cpf", "123.456.789-10",
                "phone", "(85)999999999",
                "address", "Rua das Flores")
        );

        ResultActions response = mockMvc.perform(
                post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(auth)
                        .content(invalidJson)
        );

        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation error")));
    }

    @Test
    @DisplayName("POST /customers should return 400 when the cpf field is missing")
    void createCustomer_ShouldReturnBadRequest_WhenCPFFieldMissing() throws Exception {

        String invalidJson = createJson(Map.of(
                "name", "João",
                "cpf", "",
                "phone", "(85)999999999",
                "address", "Rua das Flores")
        );

        ResultActions response = mockMvc.perform(
                post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(auth)
                        .content(invalidJson)
        );

        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation error")));
    }

    @Test
    @DisplayName("POST /customers should return 400 when the cpf field is invalid")
    void createCustomer_ShouldReturnBadRequest_WhenCPFFieldInvalid() throws Exception {

        String invalidJson = createJson(Map.of(
                "name", "João",
                "cpf", "123.456.789-10",
                "phone", "(85)999999999",
                "address", "Rua das Flores")
        );

        ResultActions response = mockMvc.perform(
                post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(auth)
                        .content(invalidJson)
        );

        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation error")));
    }

    @Test
    @DisplayName("GET /customers should return 200 OK and customer list")
    void getAllCustomers_ShouldReturnCustomerList() throws Exception {
        List<Customer> customers = new ArrayList<>();

        CustomerRequestDTO dto2 = createCustomerRequestDTO(
                "Maria",
                "657.573.120-17",
                "(85)995511457",
                "Rua das Pragatas"
        );
        Customer customer1 = createCustomer(dto2, user);

        customers.add(customer);
        customers.add(customer1);

        var page = new PageImpl<>(customers, PageRequest.of(0, 10), customers.size());
        given(customerService.findAll(any(Pageable.class))).willReturn(page);

        ResultActions response = mockMvc.perform(
                get("/customers")
                        .principal(auth)
        );

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.content.size()", is(customers.size())));

    }

    @Test
    @DisplayName("GET /customers should return 404 NotFoundException when customer list not found")
    void getAllCustomers_ShouldReturnNotFoundException_WhenCustomerListNotFound() throws Exception {
        given(customerService.findAll(any(Pageable.class))).willThrow(new NotFoundException("Customers not found"));

        ResultActions response = mockMvc.perform(
                get("/customers")
                        .principal(auth)
        );

        response.andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(jsonPath("$.message", is("Customers not found")));
    }

    @Test
    @DisplayName("GET /customers/{email} should return customer when valid ID is provided")
    void getCustomerById_ShouldReturnCustomer_WhenValidIdProvided() throws Exception {
        given(customerService.findByEmail(customer.getUser().getEmail())).willReturn(customer);

        ResultActions response = mockMvc.perform(
                get("/customers/{email}", customer.getUser().getEmail())
                        .principal(auth)
        );

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.name", is(customer.getName()))).andExpect(jsonPath("$.cpf", is(customer.getCpf())))
                .andExpect(jsonPath("$.phone", is(customer.getPhone())))
                .andExpect(jsonPath("$.address", is(customer.getAddress())));
    }

    @Test
    @DisplayName("GET /customers/{email} should return 404 NotFoundException when invalid ID is provided")
    void getCustomerById_ShouldReturnNotFoundException_WhenInvalidIdProvided() throws Exception {
        given(customerService.findByEmail(any(String.class))).willThrow(new NotFoundException("Customer not found"));

        ResultActions response = mockMvc.perform(
                get("/customers/{email}", customer.getUser().getEmail())
                        .principal(auth)
        );

        response.andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(jsonPath("$.message", is("Customer not found")));
    }

    @Test
    @DisplayName("PUT /customers should return 200 OK with updated customer object")
    void updateCustomer_ShouldReturnOkWithUpdatedCustomer_WhenUpdateRequestIsValid() throws Exception {
        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(customer.getId());
        updatedCustomer.setName("Joao da Silva");
        updatedCustomer.setPhone("(85)988128522");
        updatedCustomer.setAddress("Rua das Oliveiras, 211, Centro, São Paulo - SP");
        updatedCustomer.setUser(user);

        given(customerService.update(any(String.class), any(CustomerRequestDTO.class))).willReturn(updatedCustomer);

        ResultActions response = mockMvc.perform(
                put("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                        .principal(auth)
                        .content(objectMapper.writeValueAsString(updatedCustomer))
        );

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.name", is(updatedCustomer.getName())))
                .andExpect(jsonPath("$.phone", is(updatedCustomer.getPhone())))
                .andExpect(jsonPath("$.address", is(updatedCustomer.getAddress())));
    }

    @Test
    @DisplayName("PUT /customers should return 404 NotFoundException when invalid ID is provided")
    void updateCustomer_ShouldReturnNotFoundException_WhenInvalidIdProvided() throws Exception {
        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(customer.getId());
        updatedCustomer.setName("Joao da Silva");
        updatedCustomer.setPhone("(85)988128522");
        updatedCustomer.setAddress("Rua das Oliveiras, 211, Centro, São Paulo - SP");

        given(customerService.update(any(String.class), any(CustomerRequestDTO.class))).willThrow(new NotFoundException("Customer not found"));

        ResultActions response = mockMvc.perform(
                put("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(auth)
                        .content(objectMapper.writeValueAsString(updatedCustomer))
        );

        response.andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(jsonPath("$.message", is("Customer not found")));
    }

    @Test
    @DisplayName("DELETE /customers should return 204 No Content when customer exists")
    void deleteCustomer_ShouldReturnNoContent_WhenCustomerExists() throws Exception {
        willDoNothing().given(customerService).delete(any(String.class));

        ResultActions response = mockMvc.perform(delete("/customers")
                .principal(auth)
        );

        response.andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @DisplayName("DELETE /customers should return 404 NotFoundException when customer does not exists")
    void deleteCustomer_ShouldReturnNotFoundException_WhenCustomerDoesNotExists() throws Exception {
        willThrow(new NotFoundException("Customer not found")).given(customerService).delete(any(String.class));

        ResultActions response = mockMvc.perform(delete("/customers")
                .principal(auth)
        );

        response.andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(jsonPath("$.message", is("Customer not found")));
    }
    
}
