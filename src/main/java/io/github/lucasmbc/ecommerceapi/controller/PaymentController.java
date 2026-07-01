package io.github.lucasmbc.ecommerceapi.controller;

import io.github.lucasmbc.ecommerceapi.controller.dto.response.PaymentResponseDTO;
import io.github.lucasmbc.ecommerceapi.controller.mapper.PaymentMapper;
import io.github.lucasmbc.ecommerceapi.domain.model.PaymentType;
import io.github.lucasmbc.ecommerceapi.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
@Tag(name = "Payments", description = "Payments API")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/{orderId}")
    @Operation(summary = "Pay for an order", description = "Pay for an order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment created"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
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
