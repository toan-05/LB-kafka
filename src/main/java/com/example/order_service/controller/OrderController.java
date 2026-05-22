package com.example.order_service.controller;

import com.example.order_service.dto.request.CreateOrderRequest;
import com.example.order_service.dto.request.UpdateOrderRequest;
import com.example.order_service.dto.response.OrderResponse;
import jakarta.validation.Valid;
import com.example.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for order operations.
 */
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Creates a new order and starts inventory reservation.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return OrderResponse.from(orderService.createOrder(request));
    }

    /**
     * Returns one order by id.
     */
    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable Long id) {
        return OrderResponse.from(orderService.getOrder(id));
    }

    /**
     * Returns all orders.
     */
    @GetMapping
    public List<OrderResponse> getOrders() {
        return orderService.getOrders().stream()
                .map(OrderResponse::from)
                .toList();
    }

    /**
     * Replaces an order while it is still editable.
     */
    @PutMapping("/{id}")
    public OrderResponse updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderRequest request
    ) {
        return OrderResponse.from(orderService.updateOrder(id, request));
    }

    /**
     * Deletes an order while it is still editable.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }
}
