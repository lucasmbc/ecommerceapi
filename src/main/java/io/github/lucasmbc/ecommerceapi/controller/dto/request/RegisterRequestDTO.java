package io.github.lucasmbc.ecommerceapi.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Data Transfer Object for user registration requests.")
public class RegisterRequestDTO {

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email should be valid")
    @Schema(description = "User's email address for registration.", example = "newuser@example.com")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password must be at least six characters long")
    @Schema(description = "User's password for registration (minimum 6 characters).", example = "strongpass123")
    private String password;

    @NotBlank(message = "Role cannot be blank")
    @Schema(description = "User's role (e.g., 'USER', 'ADMIN').", example = "USER")
    private String role;

    public RegisterRequestDTO() {
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}