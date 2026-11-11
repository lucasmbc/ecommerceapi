package io.github.lucasmbc.ecommerceapi.service.payment;

import io.github.lucasmbc.ecommerceapi.domain.model.Order;
import io.github.lucasmbc.ecommerceapi.domain.model.PaymentStatus;
import io.github.lucasmbc.ecommerceapi.domain.model.PaymentType;
import org.springframework.stereotype.Component;

@Component
public class BoletoPaymentStrategy implements PaymentStrategy {
    @Override
    public PaymentType getPaymentType() {
        return PaymentType.BOLETO;
    }

    @Override
    public PaymentStatus process(Order order) {
        return PaymentStatus.PENDING;
    }
}
