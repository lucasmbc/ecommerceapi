package io.github.lucasmbc.ecommerceapi.controller;

import io.github.lucasmbc.ecommerceapi.domain.model.Customer;
import io.github.lucasmbc.ecommerceapi.service.CustomerService;
import io.github.lucasmbc.ecommerceapi.service.exception.CpfAlreadyExistsException;
import io.github.lucasmbc.ecommerceapi.service.exception.EmailAlreadyExistsException;
import io.github.lucasmbc.ecommerceapi.service.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService customerService;

    private Customer customer;

    @BeforeEach
    public void setup() {
        customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setName("Joao");
        customer.setEmail("joao@email.com");
        customer.setPassword("123456");
        customer.setCpf("892.334.220-38");
    }

    private String createInvalidJson(String name, String email, String password, String cpf) {
        return String.format("""
        {
            "name": "%s",
            "email": "%s",
            "password": "%s",
            "cpf": "%s"
        }
        """, name, email, password, cpf);
    }

    @Test
    @DisplayName("POST /customers should create customer and return 201 created customer")
    void createCustomer_ShouldReturnCreatedCustomer() throws Exception {
        given(customerService.create(any(Customer.class))).willAnswer((invocation) -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(
                post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer))
        );

        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(customer.getName())))
                .andExpect(jsonPath("$.email", is(customer.getEmail())));
    }

    @Test
    @DisplayName("POST /customers should return 409 EmailAlreadyExistsException when email already exists")
    void createCustomer_ShouldReturnEmailAlreadyExistsException_WhenEmailAlreadyExists() throws Exception {
        given(customerService.create(any(Customer.class))).willThrow(new EmailAlreadyExistsException(customer.getEmail()));

        ResultActions response = mockMvc.perform(
                post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer))
        );

        response.andExpect(status().isConflict())
                .andDo(print())
                .andExpect(jsonPath("$.message", is("Email already registered: " + customer.getEmail())));
    }

    @Test
    @DisplayName("POST /customers should return 409 CpfAlreadyExistsException when cpf already exists")
    void createCustomer_ShouldReturnCpfAlreadyExistsException_WhenCpfAlreadyExists() throws Exception {
        given(customerService.create(any(Customer.class))).willThrow(new CpfAlreadyExistsException(customer.getCpf()));

        ResultActions response = mockMvc.perform(
                post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer))
        );

        response.andExpect(status().isConflict())
                .andDo(print())
                .andExpect(jsonPath("$.message", is("CPF already registered: " + customer.getCpf())));
    }

    @Test
    @DisplayName("POST /customers should return 400 when the name field is missing")
    void createCustomer_ShouldReturnBadRequest_WhenNameFieldMissing() throws Exception {

        String invalidJson = createInvalidJson("", "joao@email.com", "123456", "763.625.040-38");

        ResultActions response = mockMvc.perform(
                post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson)
        );

        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation error")));
    }

    @Test
    @DisplayName("POST /customers should return 400 when the email field is missing")
    void createCustomer_ShouldReturnBadRequest_WhenEmailFieldMissing() throws Exception {

        String invalidJson = createInvalidJson("João", "", "123456", "763.625.040-38");

        ResultActions response = mockMvc.perform(
                post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson)
        );

        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation error")));
    }

    @Test
    @DisplayName("POST /customers should return 400 when the email field is invalid")
    void createCustomer_ShouldReturnBadRequest_WhenEmailFieldInvalid() throws Exception {

        String invalidJson = createInvalidJson("João", "joaoemail.com", "123456", "763.625.040-38");

        ResultActions response = mockMvc.perform(
                post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson)
        );

        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation error")));
    }

    @Test
    @DisplayName("POST /customers should return 400 when the password field is missing")
    void createCustomer_ShouldReturnBadRequest_WhenPasswordFieldMissing() throws Exception {

        String invalidJson = createInvalidJson("João", "joao@email.com", "", "763.625.040-38");

        ResultActions response = mockMvc.perform(
                post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson)
        );

        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation error")));
    }

    @Test
    @DisplayName("POST /customers should return 400 when the password field is invalid")
    void createCustomer_ShouldReturnBadRequest_WhenPasswordFieldInvalid() throws Exception {

        String invalidJson = createInvalidJson("João", "joao@email.com", "123", "763.625.040-38");

        ResultActions response = mockMvc.perform(
                post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson)
        );

        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation error")));
    }

    @Test
    @DisplayName("POST /customers should return 400 when the cpf field is missing")
    void createCustomer_ShouldReturnBadRequest_WhenCPFFieldMissing() throws Exception {

        String invalidJson = createInvalidJson("João", "joao@email.com", "123456", "");

        ResultActions response = mockMvc.perform(
                post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson)
        );

        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation error")));
    }

    @Test
    @DisplayName("POST /customers should return 400 when the cpf field is invalid")
    void createCustomer_ShouldReturnBadRequest_WhenCPFFieldInvalid() throws Exception {

        String invalidJson = createInvalidJson("João", "joao@email.com", "123456", "123.456.789-10");

        ResultActions response = mockMvc.perform(
                post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
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

        Customer customer1 = new Customer();
        customer1.setName("Maria");
        customer1.setEmail("maria@email.com");
        customer1.setPassword("123456");
        customer1.setCpf("657.573.120-17");

        customers.add(customer);
        customers.add(customer1);

        given(customerService.findAll()).willReturn(customers);

        ResultActions response = mockMvc.perform(get("/customers"));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(customers.size())));

    }

    @Test
    @DisplayName("GET /customers should return 404 NotFoundException when customer list not found")
    void getAllCustomers_ShouldReturnNotFoundException_WhenCustomerListNotFound() throws Exception {
        given(customerService.findAll()).willThrow(new NotFoundException("Customers not found"));

        ResultActions response = mockMvc.perform(get("/customers"));

        response.andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(jsonPath("$.message", is("Customers not found")));
    }

    @Test
    @DisplayName("GET /customers/{id} should return customer when valid ID is provided")
    void getCustomerById_ShouldReturnCustomer_WhenValidIdProvided() throws Exception {
        given(customerService.findById(any(UUID.class))).willReturn(customer);

        ResultActions response = mockMvc.perform(get("/customers/{id}", customer.getId()));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.name", is(customer.getName())))
                .andExpect(jsonPath("$.email", is(customer.getEmail())));
    }

    @Test
    @DisplayName("GET /customers/{id} should return 404 NotFoundException when invalid ID is provided")
    void getCustomerById_ShouldReturnNotFoundException_WhenInvalidIdProvided() throws Exception {
        given(customerService.findById(any(UUID.class))).willThrow(new NotFoundException("Customer not found"));

        ResultActions response = mockMvc.perform(get("/customers/{id}", customer.getId()));

        response.andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(jsonPath("$.message", is("Customer not found")));
    }

    @Test
    @DisplayName("PUT /customers/{id} should return 200 OK with updated customer object")
    void updateCustomer_ShouldReturnOkWithUpdatedCustomer_WhenUpdateRequestIsValid() throws Exception {
        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(customer.getId());
        updatedCustomer.setName("Joao da Silva");
        updatedCustomer.setPhone("(85)988128522");
        updatedCustomer.setAddress("Rua das Oliveiras, 211, Centro, São Paulo - SP");

        given(customerService.update(any(UUID.class), any(Customer.class))).willReturn(updatedCustomer);

        ResultActions response = mockMvc.perform(
                put("/customers/{id}", updatedCustomer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCustomer))
        );

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.name", is(updatedCustomer.getName())))
                .andExpect(jsonPath("$.phone", is(updatedCustomer.getPhone())))
                .andExpect(jsonPath("$.address", is(updatedCustomer.getAddress())));
    }

    @Test
    @DisplayName("PUT /customers/{id} should return 404 NotFoundException when invalid ID is provided")
    void updateCustomer_ShouldReturnNotFoundException_WhenInvalidIdProvided() throws Exception {
        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(customer.getId());
        updatedCustomer.setName("Joao da Silva");
        updatedCustomer.setPhone("(85)988128522");
        updatedCustomer.setAddress("Rua das Oliveiras, 211, Centro, São Paulo - SP");

        given(customerService.update(any(UUID.class), any(Customer.class))).willThrow(new NotFoundException("Customer not found"));

        ResultActions response = mockMvc.perform(
                put("/customers/{id}", customer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCustomer))
        );

        response.andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(jsonPath("$.message", is("Customer not found")));
    }

    @Test
    @DisplayName("DELETE /customers/{id} should return 204 No Content when customer exists")
    void deleteCustomer_ShouldReturnNoContent_WhenCustomerExists() throws Exception {
        willDoNothing().given(customerService).delete(any(UUID.class));

        ResultActions response = mockMvc.perform(delete("/customers/{id}", customer.getId()));

        response.andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @DisplayName("DELETE /customers/{id} should return 404 NotFoundException when customer does not exists")
    void deleteCustomer_ShouldReturnNotFoundException_WhenCustomerDoesNotExists() throws Exception {
        willThrow(new NotFoundException("Customer not found")).given(customerService).delete(any(UUID.class));

        ResultActions response = mockMvc.perform(delete("/customers/{id}", customer.getId()));

        response.andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(jsonPath("$.message", is("Customer not found")));
    }
    
}
