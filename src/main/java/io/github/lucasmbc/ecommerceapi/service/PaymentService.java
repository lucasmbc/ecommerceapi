package io.github.lucasmbc.ecommerceapi.service;

import io.github.lucasmbc.ecommerceapi.domain.model.*;
import io.github.lucasmbc.ecommerceapi.domain.repository.OrderRepository;
import io.github.lucasmbc.ecommerceapi.domain.repository.PaymentRepository;
import io.github.lucasmbc.ecommerceapi.service.exception.NotFoundException;
import io.github.lucasmbc.ecommerceapi.service.payment.PaymentStrategy;
import io.github.lucasmbc.ecommerceapi.service.payment.PaymentStrategyFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentStrategyFactory paymentStrategyFactory;

    public  PaymentService(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            PaymentStrategyFactory paymentStrategyFactory
    ) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.paymentStrategyFactory = paymentStrategyFactory;
    }

    public Payment pay(UUID orderId, PaymentType type) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        PaymentStrategy strategy = paymentStrategyFactory.getStrategy(type);

        PaymentStatus status = strategy.process(order);

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentType(type);
        payment.setStatus(status);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setAmount(order.getTotal());

        if(status == PaymentStatus.APPROVED) {
            order.setStatus(OrderStatus.PAID);
        }

        return paymentRepository.save(payment);
    }
}
