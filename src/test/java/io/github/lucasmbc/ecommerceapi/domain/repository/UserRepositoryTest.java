package io.github.lucasmbc.ecommerceapi.domain.repository;

import io.github.lucasmbc.ecommerceapi.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should find a user by email when it exists")
    void findByEmail_shouldReturnUser_whenEmailExists() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setRole("USER");
        userRepository.save(user);

        // Act
        Optional<User> foundUserOptional = userRepository.findByEmail("test@example.com");

        // Assert
        assertThat(foundUserOptional).isPresent();
        assertThat(foundUserOptional.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("Should return empty Optional when searching for an email that does not exist")
    void findByEmail_shouldReturnEmpty_whenEmailDoesNotExist() {
        // Act
        Optional<User> foundUserOptional = userRepository.findByEmail("nonexistent@example.com");

        // Assert
        assertThat(foundUserOptional).isEmpty();
    }

    @Test
    @DisplayName("Should return true when the email exists")
    void existsByEmail_shouldReturnTrue_whenEmailExists() {
        // Arrange
        User user = new User();
        user.setEmail("exists@example.com");
        user.setPassword("password123");
        user.setRole("USER");
        userRepository.save(user);

        // Act & Assert
        assertThat(userRepository.existsByEmail("exists@example.com")).isTrue();
    }

    @Test
    @DisplayName("Should return false when the email does not exist")
    void existsByEmail_shouldReturnFalse_whenEmailDoesNotExist() {
        // Act & Assert
        assertThat(userRepository.existsByEmail("nonexistent@example.com")).isFalse();
    }
}