package io.github.lucasmbc.ecommerceapi.controller;

import io.github.lucasmbc.ecommerceapi.controller.dto.request.LoginRequestDTO;
import io.github.lucasmbc.ecommerceapi.controller.dto.request.RegisterRequestDTO;
import io.github.lucasmbc.ecommerceapi.controller.dto.response.AuthResponseDTO;
import io.github.lucasmbc.ecommerceapi.controller.mapper.AuthMapper;
import io.github.lucasmbc.ecommerceapi.controller.mapper.UserMapper;
import io.github.lucasmbc.ecommerceapi.domain.model.User;
import io.github.lucasmbc.ecommerceapi.service.JwtService;
import io.github.lucasmbc.ecommerceapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserService userService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO dto) {

        User user = UserMapper.toEntity(dto);
        User saved = userService.register(user);
        var token = jwtService.generateToken(
                saved.getEmail(),
                saved.getRole()
        );

        AuthResponseDTO authResponseDTO = AuthMapper.toResponse(token);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).body(authResponseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid  @RequestBody LoginRequestDTO dto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(), dto.getPassword()
                )
        );

        User user = userService.findByEmail(dto.getEmail());

        var token = jwtService.generateToken(
                user.getEmail(),
                user.getRole()
        );

        AuthResponseDTO authResponseDTO = AuthMapper.toResponse(token);

        return ResponseEntity.ok(authResponseDTO);
    }
}
