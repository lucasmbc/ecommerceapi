package io.github.lucasmbc.ecommerceapi.service;

import io.github.lucasmbc.ecommerceapi.domain.model.User;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    // Cria um mock para cada dependência da classe de serviço
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    // Injeta os mocks criados acima na instância de UserService
    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setPassword("rawPassword123");
        user.setRole("USER");
    }

    @Test
    @DisplayName("Should register user successfully")
    void register_shouldRegisterUserSuccessfully() {
        // Arrange (Preparação)
        given(userRepository.existsByEmail(user.getEmail())).willReturn(false);
        given(passwordEncoder.encode("rawPassword123")).willReturn("encodedPassword");
        given(userRepository.save(user)).willReturn(user);

        // Act (Ação)
        User registeredUser = userService.register(user);

        // Assert (Verificação)
        assertNotNull(registeredUser);
        assertEquals("USER", registeredUser.getRole());
        assertEquals("encodedPassword", registeredUser.getPassword());
        assertEquals(user.getEmail(), registeredUser.getEmail());

        // Verifica se os métodos dos mocks foram chamados como esperado
        then(userRepository).should(times(1)).save(user);
        then(userRepository).should().existsByEmail(user.getEmail());
        then(passwordEncoder).should().encode("rawPassword123");
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException when trying to register a duplicate email")
    void register_shouldThrowEmailAlreadyExistsExceptionWhenEmailIsAlreadyRegistered() {
        // Arrange
        given(userRepository.existsByEmail(user.getEmail())).willThrow(new EmailAlreadyExistsException(user.getEmail()));

        // Act & Assert
        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.register(user)
        );

        assertEquals("Email already registered: " + user.getEmail(), exception.getMessage());

        // Verifica que os métodos seguintes não foram chamados
        then(passwordEncoder).shouldHaveNoInteractions();
        then(userRepository).should().existsByEmail(user.getEmail());
    }

    @Test
    @DisplayName("Should find a user by email successfully")
    void findByEmail_shouldFindUserByEmailSuccessfully() {
        // Arrange
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

        // Act
        User foundUser = userService.findByEmail(user.getEmail());

        // Assert
        assertNotNull(foundUser);
        assertEquals(user.getId(), foundUser.getId());
        then(userRepository).should().findByEmail(user.getEmail());
    }

    @Test
    @DisplayName("Should throw NotFoundException when user is not found by email")
    void findByEmail_shouldThrowNotFoundExceptionWhenUserIsNotFoundByEmail() {
        // Arrange
        String nonExistentEmail = "notfound@example.com";
        given(userRepository.findByEmail(nonExistentEmail)).willThrow(new NotFoundException("User not found"));

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.findByEmail(nonExistentEmail)
        );

        assertEquals("User not found", exception.getMessage());
        then(userRepository).should().findByEmail(nonExistentEmail);
    }
}
