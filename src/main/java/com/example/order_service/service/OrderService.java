package com.example.order_service.service;

import com.example.order_service.dto.request.CreateOrderRequest;
import com.example.order_service.dto.request.UpdateOrderRequest;
import com.example.order_service.entity.Order;

import java.util.List;

/**
 * Handles order lifecycle operations.
 */
public interface OrderService {

    /**
     * Creates an order and publishes an event for inventory reservation.
     */
    Order createOrder(CreateOrderRequest request);

    /**
     * Returns one order by id.
     */
    Order getOrder(Long id);

    /**
     * Returns all orders.
     */
    List<Order> getOrders();

    /**
     * Replaces an order while it is still editable.
     */
    Order updateOrder(Long id, UpdateOrderRequest request);

    /**
     * Deletes an order while it is still editable.
     */
    void deleteOrder(Long id);

    /**
     * Marks an order as inventory-reserved after the inventory result event is received.
     */
    Order markInventoryReserved(Long orderId, String processedBy);

    /**
     * Marks an order as inventory-rejected after the inventory result event is received.
     */
    Order markInventoryRejected(Long orderId, String reason, String processedBy);
}
