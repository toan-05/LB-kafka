package com.example.order_service.entity.dto.response;

import com.example.order_service.entity.Order;
import com.example.order_service.entity.enums.OrderStatus;

import java.math.BigDecimal;

public record OrderResponse(
        Long id,
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalAmount,
        OrderStatus status,
        String statusReason,
        String handledBy
) {

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getUnitPrice(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getStatusReason(),
                order.getHandledBy()
        );
    }
}
