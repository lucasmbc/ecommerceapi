package io.github.lucasmbc.ecommerceapi.domain.repository;

import io.github.lucasmbc.ecommerceapi.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @DisplayName("JUnit test should find payment by orderId")
    @Test
    void shouldFindPaymentByOrderId() {

        Order order = new Order();
        order.setTotal(BigDecimal.valueOf(100));
        order.setStatus(OrderStatus.PENDING);
        orderRepository.save(order);

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentType(PaymentType.PIX);
        payment.setStatus(PaymentStatus.APPROVED);
        payment.setAmount(BigDecimal.valueOf(100));

        paymentRepository.save(payment);

        var result = paymentRepository.findByOrderId(order.getId()).get();

        assertNotNull(result);
        assertEquals(order.getId(), result.getOrder().getId());
    }
}