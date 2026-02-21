package io.github.lucasmbc.ecommerceapi.controller.mapper;

import io.github.lucasmbc.ecommerceapi.controller.dto.response.PaymentResponseDTO;
import io.github.lucasmbc.ecommerceapi.domain.model.Payment;

public class PaymentMapper {

    public PaymentMapper() {
    }

    public static PaymentResponseDTO toResponse(Payment payment) {
        return new PaymentResponseDTO(
                payment.getOrder().getId(),
                payment.getStatus().name(),
                payment.getPaymentType().name(),
                payment.getAmount(),
                payment.getPaymentDate()
        );
    }
}
