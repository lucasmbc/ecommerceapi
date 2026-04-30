package io.github.lucasmbc.ecommerceapi.controller;

import io.github.lucasmbc.ecommerceapi.controller.dto.request.LoginRequestDTO;
import io.github.lucasmbc.ecommerceapi.controller.dto.request.RegisterRequestDTO;
import io.github.lucasmbc.ecommerceapi.domain.model.User;
import io.github.lucasmbc.ecommerceapi.service.JwtService;
import io.github.lucasmbc.ecommerceapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private RegisterRequestDTO request;
    private User user;

    @BeforeEach
    void setUp() {
        request = new RegisterRequestDTO();
        request.setEmail("test@email.com");
        request.setPassword("123456");
        request.setRole("USER");

        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole());
    }

    private String createJson(Map<String, Object> fields) throws Exception {
        return objectMapper.writeValueAsString(fields);
    }

    @Test
    @DisplayName("POST /auth/register should register user and return jwt token")
    void shouldRegisterUserAndReturnJwtToken() throws Exception {
        given(userService.register(any(User.class))).willReturn(user);
        given(jwtService.generateToken(any(String.class), any(String.class))).willReturn("token");

        ResultActions response = mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", is("token")));

        then(userService).should().register(any(User.class));
        then(jwtService).should().generateToken(any(String.class), any(String.class));
    }

    @Test
    @DisplayName("POST /auth/login should authenticate user and return jwt token")
    void shouldAuthenticateAndReturnJwtToken() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("test@email.com");
        loginRequest.setPassword("123456");

        given(userService.findByEmail("test@email.com")).willReturn(user);
        given(jwtService.generateToken(any(String.class), any(String.class))).willReturn("token");

        ResultActions response = mockMvc.perform(
                post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest))
        );

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("token")));

        then(authenticationManager).should()
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        then(userService).should().findByEmail("test@email.com");
        then(jwtService).should().generateToken("test@email.com", "USER");
    }

    @Test
    @DisplayName("POST /auth/register should return 400 when the email field is missing")
    void registerUser_ShouldReturnBadRequest_WhenEmailFieldMissing() throws Exception {

        String invalidJson = createJson(Map.of("email", "", "password", "123456", "role", "USER"));

        ResultActions response = mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson)
        );

        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation error")));
    }

    @Test
    @DisplayName("POST /auth/register should return 400 when the email field is invalid")
    void registerUser_ShouldReturnBadRequest_WhenEmailFieldInvalid() throws Exception {

        String invalidJson = createJson(Map.of("email", "email", "password", "123456", "role", "USER"));

        ResultActions response = mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson)
        );

        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation error")));
    }

    @Test
    @DisplayName("POST /auth/register should return 400 when the password field is missing")
    void registerUser_ShouldReturnBadRequest_WhenPasswordFieldMissing() throws Exception {

        String invalidJson = createJson(Map.of("email", "test@email.com", "password", "", "role", "USER"));

        ResultActions response = mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson)
        );

        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation error")));
    }

    @Test
    @DisplayName("POST /auth/register should return 400 when the password field is invalid")
    void registerUser_ShouldReturnBadRequest_WhenPasswordFieldInvalid() throws Exception {

        String invalidJson = createJson(Map.of("email", "teste@email.com", "password", "123", "role", "USER"));

        ResultActions response = mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson)
        );

        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation error")));
    }

    @Test
    @DisplayName("POST /auth/login should return 400 when the email field is missing")
    void loginUser_ShouldReturnBadRequest_WhenEmailFieldMissing() throws Exception {
        String invalidJson = createJson(Map.of("email", "", "password", "123456"));

        ResultActions response = mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson)
        );

        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation error")));
    }

    @Test
    @DisplayName("POST /auth/login should return 400 when the email field is invalid")
    void loginUser_ShouldReturnBadRequest_WhenEmailFieldInvalid() throws Exception {
        String invalidJson = createJson(Map.of("email", "invalid-email", "password", "123456"));

        ResultActions response = mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson)
        );

        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation error")));
    }

    @Test
    @DisplayName("POST /auth/login should return 400 when the password field is missing")
    void loginUser_ShouldReturnBadRequest_WhenPasswordFieldMissing() throws Exception {
        String invalidJson = createJson(Map.of("email", "test@email.com", "password", ""));

        ResultActions response = mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson)
        );

        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation error")));
    }

}
