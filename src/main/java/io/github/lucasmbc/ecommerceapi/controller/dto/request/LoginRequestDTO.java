package io.github.lucasmbc.ecommerceapi.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Data Transfer Object for user login requests.")
public class LoginRequestDTO {
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email should be valid")
    @Schema(description = "User's email address for login.", example = "user@example.com")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Schema(description = "User's password for login.", example = "securepassword123")
    private String password;

    public LoginRequestDTO() {
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
}