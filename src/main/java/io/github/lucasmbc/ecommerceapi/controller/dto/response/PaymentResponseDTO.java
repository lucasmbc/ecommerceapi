package io.github.lucasmbc.ecommerceapi.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Data Transfer Object for payment response, containing payment details.")
public record PaymentResponseDTO(
        @Schema(description = "Unique identifier of the order associated with this payment.", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
        UUID orderId,
        @Schema(description = "Status of the payment (e.g., PENDING, COMPLETED, FAILED).", example = "COMPLETED")
        String status,
        @Schema(description = "Type of payment used (e.g., CREDIT_CARD, BANK_TRANSFER, PIX).", example = "CREDIT_CARD")
        String paymentType,
        @Schema(description = "Amount of the payment.", example = "250.75")
        BigDecimal amount,
        @Schema(description = "Date and time when the payment was processed.", example = "2023-10-26T10:35:00")
        LocalDateTime paymentDate
) {}