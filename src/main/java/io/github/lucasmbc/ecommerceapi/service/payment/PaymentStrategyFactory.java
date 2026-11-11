package io.github.lucasmbc.ecommerceapi.service.payment;

import io.github.lucasmbc.ecommerceapi.domain.model.PaymentType;
import io.github.lucasmbc.ecommerceapi.service.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class PaymentStrategyFactory {

    private final Map<PaymentType, PaymentStrategy> strategies;

    public PaymentStrategyFactory(List<PaymentStrategy> strategies) {
        this.strategies = new EnumMap<>(PaymentType.class);

        for(PaymentStrategy strategy : strategies) {
            PaymentStrategy previous = this.strategies.put(
                    strategy.getPaymentType(),
                    strategy
            );

            if(previous != null) {
                throw new IllegalStateException("More than one strategy configured for payment type " + strategy.getPaymentType());
            }
        }
    }

    public PaymentStrategy getStrategy(PaymentType paymentType) {
        if(paymentType == null) {
            throw new BusinessException("Payment type is required");
        }

        PaymentStrategy strategy = this.strategies.get(paymentType);

        if(strategy == null) {
            throw new BusinessException("Payment type not supported: " + paymentType);
        }

        return strategy;
    }
}
