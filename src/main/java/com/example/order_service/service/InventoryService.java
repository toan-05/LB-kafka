package com.example.order_service.service;

import com.example.order_service.event.InventoryResultEvent;
import com.example.order_service.event.OrderCreatedEvent;

/**
 * Handles stock reservation after an order-created event is received.
 */
public interface InventoryService {

    /**
     * Reserves stock for an order-created event and returns the reservation result.
     */
    InventoryResultEvent reserveInventory(OrderCreatedEvent event, String processedBy);
}
