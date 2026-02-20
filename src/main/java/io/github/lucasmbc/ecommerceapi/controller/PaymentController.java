package io.github.lucasmbc.ecommerceapi.controller;

import io.github.lucasmbc.ecommerceapi.controller.dto.response.PaymentResponseDTO;
import io.github.lucasmbc.ecommerceapi.controller.mapper.PaymentMapper;
import io.github.lucasmbc.ecommerceapi.domain.model.Payment;
import io.github.lucasmbc.ecommerceapi.domain.model.PaymentType;
import io.github.lucasmbc.ecommerceapi.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/{orderId}")
    public ResponseEntity<PaymentResponseDTO> pay(@PathVariable String orderId, @RequestParam PaymentType paymentType) {
        var saved = paymentService.pay(UUID.fromString(orderId), paymentType);
        var paymentResponseDTO = PaymentMapper.toResponse(saved);

        URI location =  ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{paymentId}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).body(paymentResponseDTO);
    }
}
