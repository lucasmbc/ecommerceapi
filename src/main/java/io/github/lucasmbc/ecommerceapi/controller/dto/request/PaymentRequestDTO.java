package io.github.lucasmbc.ecommerceapi.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data Transfer Object for payment requests.")
public class PaymentRequestDTO {

    @Schema(description = "Type of payment (e.g., 'CREDIT_CARD', 'BANK_TRANSFER', 'PIX').", example = "CREDIT_CARD")
    private String paymentType;

    public PaymentRequestDTO() {
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }
}