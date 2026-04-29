package io.github.lucasmbc.ecommerceapi.config;

import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtConfig {

    @Bean
    public SecretKey secretKey() {
        String secret = System.getenv("JWT_SECRET");
        if(secret == null || secret.isEmpty()) {
            throw new IllegalStateException("JWT_SECRET environment variable not set");
        }
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
