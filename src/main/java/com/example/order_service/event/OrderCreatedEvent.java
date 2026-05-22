package com.example.order_service.event;

import com.example.order_service.entity.Order;
import com.example.order_service.entity.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreatedEvent {

    private Long orderId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private OrderStatus orderStatus;
    private String createdByInstance;

    public static OrderCreatedEvent from(Order order) {
        return OrderCreatedEvent.builder()
                .orderId(order.getId())
                .productId(order.getProductId())
                .productName(order.getProductName())
                .quantity(order.getQuantity())
                .unitPrice(order.getUnitPrice())
                .totalAmount(order.getTotalAmount())
                .orderStatus(order.getOrderStatus())
                .createdByInstance(order.getHandledBy())
                .build();
    }
}
