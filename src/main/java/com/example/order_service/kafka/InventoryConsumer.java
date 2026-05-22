package com.example.order_service.kafka;

import com.example.order_service.event.InventoryResultEvent;
import com.example.order_service.event.OrderCreatedEvent;
import com.example.order_service.kafka.publisher.InventoryEventPublisher;
import com.example.order_service.service.InventoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryConsumer {

    private final InventoryService inventoryService;
    private final InventoryEventPublisher inventoryEventPublisher;
    private final String instanceName;

    public InventoryConsumer(
            InventoryService inventoryService,
            InventoryEventPublisher inventoryEventPublisher,
            @Value("${app.instance-name}") String instanceName
    ) {
        this.inventoryService = inventoryService;
        this.inventoryEventPublisher = inventoryEventPublisher;
        this.instanceName = instanceName;
    }

    @KafkaListener(
            topics = "${app.kafka.order-created-topic}",
            groupId = "inventory-service"
    )
    public void reserveInventory(OrderCreatedEvent event) {
        InventoryResultEvent result = inventoryService.reserveInventory(event, instanceName);
        inventoryEventPublisher.publishInventoryResult(result);

        System.out.println(
                "Inventory processed orderId=" + event.getOrderId()
                        + ", quantity=" + event.getQuantity()
                        + ", reserved=" + result.isReserved()
                        + ", processor=" + instanceName
        );
    }
}
