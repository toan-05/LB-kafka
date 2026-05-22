package com.example.order_service.service;

import com.example.order_service.event.InventoryResultEvent;
import com.example.order_service.event.OrderCreatedEvent;

public interface InventoryService {

    InventoryResultEvent reserveInventory(OrderCreatedEvent event, String processedBy);
}
