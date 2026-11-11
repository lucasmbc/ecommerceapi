package io.github.lucasmbc.ecommerceapi.service.payment;

import io.github.lucasmbc.ecommerceapi.domain.model.Order;
import io.github.lucasmbc.ecommerceapi.domain.model.PaymentStatus;
import io.github.lucasmbc.ecommerceapi.domain.model.PaymentType;

public interface PaymentStrategy {

    PaymentType getPaymentType();
    PaymentStatus process(Order order);

}
