package io.github.lucasmbc.ecommerceapi.controller.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponseDTO(
        UUID orderId,
        String status,
        String paymentType,
        BigDecimal amount,
        LocalDateTime paymentDate
) {}
