package io.github.lucasmbc.ecommerceapi.controller;

import io.github.lucasmbc.ecommerceapi.controller.dto.response.OrderResponseDTO;
import io.github.lucasmbc.ecommerceapi.controller.mapper.OrderMapper;
import io.github.lucasmbc.ecommerceapi.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/{customerId}")
    public ResponseEntity<OrderResponseDTO> createOrder(@PathVariable String customerId) {
        var saved = orderService.checkout(UUID.fromString(customerId));
        var orderResponseDTO = OrderMapper.toDetailResponse(saved);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).body(orderResponseDTO);
    }
}
