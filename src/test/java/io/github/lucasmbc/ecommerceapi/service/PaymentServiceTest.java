package io.github.lucasmbc.ecommerceapi.service;

import io.github.lucasmbc.ecommerceapi.domain.model.*;
import io.github.lucasmbc.ecommerceapi.domain.repository.OrderRepository;
import io.github.lucasmbc.ecommerceapi.domain.repository.PaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentService paymentService;

    @DisplayName("JUnit test should pay order")
    @Test
    void shouldPayOrder() {
        Order order = new Order();
        order.setTotal(BigDecimal.TEN);

        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));
        given(paymentRepository.save(any(Payment.class))).willAnswer(i -> i.getArgument(0));

        Payment payment = paymentService.pay(order.getId(), PaymentType.PIX);

        assertNotNull(payment);
        assertEquals(PaymentStatus.APPROVED, payment.getStatus());
        assertEquals(OrderStatus.PAID, order.getStatus());
    }

}