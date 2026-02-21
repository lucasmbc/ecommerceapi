package io.github.lucasmbc.ecommerceapi.controller.dto.request;

public class PaymentRequestDTO {

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
