package com.example.order_service.service;

import com.example.order_service.entity.dto.request.CreateOrderRequest;
import com.example.order_service.entity.Order;

import java.util.List;

public interface OrderService {

    Order createOrder(CreateOrderRequest request);

    Order getOrder(Long id);

    List<Order> getOrders();

    Order markInventoryReserved(Long orderId, String processedBy);

    Order markInventoryRejected(Long orderId, String reason, String processedBy);
}
